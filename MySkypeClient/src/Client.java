import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Connects to the server, then starts receiving messages. Creates
 * a MicThread that sends microphone data to the server, and creates an instance
 * of AudioThread for each user.
 *
 * @author dosse
 */
public class Client extends Thread {

    private Socket s;
    private ArrayList<AudioChannel> chs = new ArrayList<AudioChannel>();
    private MicThread st;

    public Client(String serverIp, int serverPort) throws IOException {
        s = new Socket(serverIp, serverPort);
    }

    @Override
    public void run() {
        try {
            ObjectInputStream fromServer = new ObjectInputStream(s
                    .getInputStream());
            ObjectOutputStream toServer = new ObjectOutputStream(s
                    .getOutputStream());
            try {
                Utils.sleep(100);
                st = new MicThread(toServer);
                st.start();
            } catch (Exception e) {
                System.out.println("mic unavailable " + e);
            }

            for (;;) {
                if (s.getInputStream().available() > 0) {
                    Message in = (Message) (fromServer.readObject());
                    AudioChannel sendTo = null;

                    for (AudioChannel ch : chs) {
                        if (ch.getChId() == in.getChId()) {
                            sendTo = ch;
                        }
                    }

                    if (sendTo != null) {
                        sendTo.addToQueue(in);
                    } else {
                        AudioChannel ch = new AudioChannel(in.getChId());
                        ch.addToQueue(in);
                        ch.start();
                        chs.add(ch);
                    }
                } else {
                    ArrayList<AudioChannel> killMe = new
                            ArrayList<>();
                    for (AudioChannel c : chs) if (c.canKill()) killMe.add(c);
                    for (AudioChannel c : killMe) {
                        c.closeAndKill();
                        chs.remove(c);
                    }
                    Utils.sleep(1);
                }
            }
        } catch (Exception e) {
            System.out.println("client err " + e.toString());
        }
    }
}