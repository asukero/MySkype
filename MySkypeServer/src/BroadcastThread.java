import java.util.ArrayList;

public class BroadcastThread extends Thread {
    private Server server = null;
    private ArrayList<Message> broadCastQueue = new ArrayList<>();

    public BroadcastThread(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        for (;;) {
            try {
                this.server.checkConnections();

                if (this.broadCastQueue.isEmpty()) {
                    Utils.sleep(10);
                    continue;
                }

                Message message = this.broadCastQueue.get(0);
                ArrayList<ClientConnection> clientConnections
                        = this.server.getClientConnections();

                for (ClientConnection clientConnection : clientConnections) {
                    // no need to broadcast to itself...
                    if (clientConnection.getID() == message.getID()) {
                        continue;
                    }

                    clientConnection.addToQueue(message);
                }

                this.broadCastQueue.remove(message);
            } catch (Throwable throwable) {
                // mutex error?
            }
        }
    }

    public void addMessage(Message message) {
        this.broadCastQueue.add(message);
    }
}