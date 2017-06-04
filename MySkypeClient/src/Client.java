import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class Client extends Thread {
    private Socket socket;
    private HashMap<Integer, SoundHandler> audioHandlers = new HashMap<>();
    private TextHandler textHandler = new TextHandler();
    private TextSender textSender;
    private String username;

    public Client(String serverIP, int serverPort, String username) throws IOException {
        this.socket = new Socket(serverIP, serverPort);
        this.username = username;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream from
                    = new ObjectInputStream(this.socket.getInputStream());
            ObjectOutputStream to
                    = new ObjectOutputStream(this.socket.getOutputStream());

            this.getTextThread(to);
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
                this.killAudioHandlers();
            }
        }
    }

    private void addAudioHandler(Message message) {
        SoundHandler newHandler = new SoundHandler(message.getID());
        newHandler.addMessageToPlay(message);
        newHandler.start();
        this.audioHandlers.put(newHandler.getID(), newHandler);
    }

    private void receiveFrom(ObjectInputStream from)
        throws IOException, ClassNotFoundException {
        Message message = (Message) from.readObject();

        if (message == null) return;

        this.handlePacket(message);
    }

    private void handlePacket(Message message) {
        Object data = message.getData();

        if (data == null) return;

        String methodToCall = "handle" + Utils.getClassName(data);

        Utils.invokeMethod(this, methodToCall, message);
    }

    private void handleSoundPacket(Message message) {
        SoundHandler sendTo = this.audioHandlers.get(message.getID());

        if (sendTo != null) {
            sendTo.addMessageToPlay(message);
        } else {
            this.addAudioHandler(message);
        }
    }

    private void handleTextPacket(Message message) {
        TextPacket textPacket = (TextPacket) message.getData();

        if (textPacket == null) return;

        this.textHandler.displayText(
            TextHandler.decompressData(textPacket.getData()),
            message.getRealDate(),
            message.getUsername());
    }

    private void killAudioHandlers() {
        this.audioHandlers.values().stream().filter(soundHandler
            -> soundHandler.canKill()).forEach(this::killAudioHandler);

        Utils.sleep(1);
    }

    private void killAudioHandler(SoundHandler soundHandler) {
        soundHandler.closeAndKill();
        this.audioHandlers.remove(soundHandler.getID());
    }

    private void getTextThread(ObjectOutputStream to) {
        try {
            Utils.sleep(100);

            this.textSender = new TextSender(to, this.username);
            textSender.start();
        } catch (Exception exception) {
            Utils.displayError("Text error: " + exception.getMessage());
        }
    }

    private void getMicrophoneThread(ObjectOutputStream to) {
        try {
            Utils.sleep(100);

            SoundSender soundSender = new SoundSender(to, this.username);
            soundSender.start();
        } catch (Exception exception) {
            Utils.displayError("Microphone error: " + exception.getMessage());
        }
    }

    public TextSender getTextSender() {
        return this.textSender;
    }
}