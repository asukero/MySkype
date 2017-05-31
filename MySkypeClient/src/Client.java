import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class Client extends Thread {
    private Socket socket;
    private HashMap<Integer, AudioChannel> audioChannels = new HashMap<>();

    public Client(String serverIP, int serverPort) throws IOException {
        this.socket = new Socket(serverIP, serverPort);
    }

    @Override
    public void run() {
        try {
            ObjectInputStream from
                    = new ObjectInputStream(this.socket.getInputStream());
            ObjectOutputStream to
                    = new ObjectOutputStream(this.socket.getOutputStream());

            this.getMicrophoneThread(to);
            this.runClient(from);
        } catch (Exception exception) {
            Utils.displayError("Client error: " + exception.getMessage());
        }
    }

    private void runClient(ObjectInputStream from)
        throws IOException, ClassNotFoundException {
        while(true) {
            if (this.socket.getInputStream().available() > 0) {
                this.receiveFrom(from);
            } else {
                this.killAudioChannels();
            }
        }
    }

    private void addAudioChannel(Message message) {
        AudioChannel newChannel = new AudioChannel(message.getID());
        newChannel.addMessageToPlay(message);
        newChannel.start();
        this.audioChannels.put(newChannel.getID(), newChannel);
    }

    private void receiveFrom(ObjectInputStream from)
        throws IOException, ClassNotFoundException {
        Message message = (Message) from.readObject();

        AudioChannel sendTo = this.audioChannels.get(message.getID());

        if (sendTo != null) {
            sendTo.addMessageToPlay(message);
        } else {
            this.addAudioChannel(message);
        }
    }

    private void killAudioChannels() {
        this.audioChannels.values().stream().filter(audioChannel
            -> audioChannel.canKill()).forEach(this::killChannel);

        Utils.sleep(1);
    }

    private void killChannel(AudioChannel audioChannel) {
        audioChannel.closeAndKill();
        this.audioChannels.remove(audioChannel.getID());
    }

    private void getMicrophoneThread(ObjectOutputStream to) {
        try {
            Utils.sleep(100);

            MicrophoneThread microphoneThread = new MicrophoneThread(to);
            microphoneThread.start();
        } catch (Exception exception) {
            Utils.displayError("Microphone error: " + exception.getMessage());
        }
    }
}