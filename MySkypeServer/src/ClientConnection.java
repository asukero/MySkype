import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class ClientConnection extends Thread {
    private Server server;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Integer ID;
    private ArrayList<Message> toSend = new ArrayList<>(); // messages to send

    public InetAddress getInetAddress() {
        return this.socket.getInetAddress();
    }

    public int getPort() {
        return this.socket.getPort();
    }

    public long getID() {
        return this.ID;
    }

    public ClientConnection(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        this.generateID();
    }

    private void generateID() {
        byte[] address = this.socket.getInetAddress().getAddress();
        this.ID = (address[0] << 16
            | address[1]
            | address[2] << 24
            | address[3] << 16) + this.socket.getPort();

        /* this.ID = (address[0] << 48
            | address[1] << 32
            | address[2] << 24
            | address[3] << 16) + this.socket.getPort(); */
    }

    public void addToQueue(Message message) {
        try {
            this.toSend.add(message);
        } catch (Throwable throwable) {
            // mutex error?
        }
    }

    private void getObjectStreams() {
        try {
            this.out = new ObjectOutputStream(this.socket.getOutputStream());
            this.in = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException IOException) {
            try {
                this.socket.close();
                Log.add("Connection error: "
                        + getInetAddress()
                        + ":"
                        + getPort()
                        + " "
                        + IOException.getMessage());
            } catch (IOException IOExceptionBis) {
                Log.add("Socket error, impossible to close: "
                        + IOExceptionBis.getMessage());
            }

            this.stopConnection();
        }
    }

    private void stopConnection() {
        this.stop();
    }

    private boolean addMessageToServer()
        throws IOException, ClassNotFoundException {
        Message toBroadcast = (Message) this.in.readObject();

        if (toBroadcast.getID() == -1) {
            toBroadcast.setID(this.ID);
            toBroadcast.setTimestamp(System.nanoTime() / 1000000L);
            this.server.addToBroadcastQueue(toBroadcast);

            return true;
        }

        return false; // invalid message
    }

    private boolean isMessageValid(Message message) {
        if ((message.getData() instanceof SoundPacket)
            && message.getTimestamp() + message.getTTL()
            >= System.nanoTime() / 1000000L) {
            return true;
        }

        Log.add("Dropping packet from " + message.getID() + " to " + this.ID);

        return false;
    }

    private boolean sendMessage(Message message) throws IOException {
        if (!this.isMessageValid(message)) return false;

        this.out.writeObject(message);
        this.toSend.remove(message);

        return true;
    }

    @Override
    public void run() {
        this.getObjectStreams();

        for (;;) {
            try {
                if (this.socket.getInputStream().available() > 0) {
                    if (!this.addMessageToServer()) continue;
                }

                if (this.toSend.isEmpty()) {
                    Utils.sleep(10);
                    continue;
                }

                this.sendMessage(this.toSend.get(0));
            } catch (IOException IOException) {
                Log.add("IOException error: " + IOException.getMessage());
            } catch (Exception exception) {
                try {
                    this.socket.close();
                } catch (IOException IOException) {
                    Log.add("Socket error, impossible to close: "
                            + IOException.getMessage());
                }

                this.stopConnection();
            } catch (Throwable throwable) {
                // mutex error?
            }
        }
    }
}
