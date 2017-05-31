import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.support.igd.PortMappingListener;
import org.teleal.cling.support.model.PortMapping;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

public class Server {
    private ArrayList<ClientConnection> clientConnections = new ArrayList<>();
    private int port;
    private ServerSocket serverSocket;
    private BroadcastThread broadcastThread;
    private UpnpService UPnPService; // point to the UPnP service when enabled

    public Server(int port, boolean isUPnP) throws Exception {
        this.port = port;

        if (isUPnP) {
            this.handleUPnP();
        }

        this.listen();
        this.broadcastThread = new BroadcastThread(this);
        this.broadcastThread.start();

        for (;;) {
            try {
                // accept new connection
                Socket socket = this.serverSocket.accept();
                ClientConnection clientConnection
                    = new ClientConnection(this, socket);

                clientConnection.start();
                this.addToClients(clientConnection);

                Log.add("new client "
                    + socket.getInetAddress()
                    + ":"
                    + socket.getPort()
                    + " on port "
                    + port);
            } catch (IOException IOException) {
                Log.add("Server error: " + IOException.getMessage());
            }
        }
    }

    private void listen() throws IOException {
        try {
            this.serverSocket = new ServerSocket(this.port);
            Log.add("Port " + port + ": server started");
        } catch (IOException IOException) {
            Log.add("Server error: " + IOException.getMessage());

            throw IOException;
        }
    }

    private void addToClients(ClientConnection clientConnection) {
        try {
            this.clientConnections.add(clientConnection);
        } catch (Throwable throwable) {
            Utils.sleep(1);
            this.addToClients(clientConnection);
        }
    }

    public void checkConnections() {
        for (ClientConnection clientConnection : clientConnections) {
            if (clientConnection.isAlive()) {
                continue;
            }

            // dead connection, which needs to be removed
            Log.add("Dead connection closed: "
                    + clientConnection.getInetAddress()
                    + ":" + clientConnection.getPort()
                    + " on port " + port);

            this.clientConnections.remove(clientConnection);
        }
    }

    private void handleUPnP() throws Exception {
        Log.add("Setting up NAT Port Forwarding...");
        Enumeration<NetworkInterface> networkInterface;

        try {
            networkInterface = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException socketException) {
            Log.add("Network error");

            throw socketException;
        }

        String IPAddress = this.getIPAddress(networkInterface);

        if (IPAddress == null) {
            Log.add("Not connected to any IPv4 network");

            throw new Exception("Network error");
        }

        this.UPnPService = new UpnpServiceImpl(new PortMappingListener(
                new PortMapping(port, IPAddress, PortMapping.Protocol.TCP)));
        this.UPnPService.getControlPoint().search();
    }

    private String getIPAddress(
            Enumeration<NetworkInterface> networkInterface) {
        while (networkInterface.hasMoreElements()) {
            NetworkInterface element = networkInterface.nextElement();
            Enumeration<InetAddress> inetAddresses
                    = element.getInetAddresses();

            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();

                if (!(inetAddress instanceof Inet4Address)) {
                    continue;
                }

                if (inetAddress.isSiteLocalAddress()) {
                    return inetAddress.getHostAddress();
                }
            }
        }

        return null;
    }

    public void addToBroadcastQueue(Message message) {
        try {
            this.broadcastThread.addMessage(message);
        } catch (Throwable throwable) {
            Utils.sleep(1);
            this.addToBroadcastQueue(message);
        }
    }

    public ArrayList<ClientConnection> getClientConnections() {
        return this.clientConnections;
    }
}
