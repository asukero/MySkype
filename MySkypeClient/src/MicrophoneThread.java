import javax.sound.sampled.LineUnavailableException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;

public class MicrophoneThread extends Microphone {
    public static double amplification = 1.0d;
    private ObjectOutputStream toServer;

    public MicrophoneThread(ObjectOutputStream toServer)
        throws LineUnavailableException {
        this.toServer = toServer;
        this.openMicrophoneLine();
    }

    private void closeMicrophoneThread() {
        try {
            this.join();
        } catch (InterruptedException interruptedException){
            System.out.print("InterruptedException error: "
                + interruptedException.getMessage());
        }
    }

    @Override
    public void run() {
        while (true) {
            if (this.microphone.available() < SoundPacket.defaultDataLength) {
                Utils.sleep(10);

                continue;
            }

            byte[] buffer = this.readFromMicrophone();

            try {
                long bufferCount = this.getBufferCount(buffer);

                if (bufferCount == 0) {
                    this.sendEmptyPacket();

                    continue;
                }

                this.sendData(buffer);
            } catch (IOException IOException) {
                System.out.print("Connection error: "
                    + IOException.getMessage());

                this.closeMicrophoneThread();
            }
        }
    }

    private ByteArrayOutputStream compressData(byte[] buffer)
        throws IOException {
        ByteArrayOutputStream byteArrayOutputStream
            = new ByteArrayOutputStream();
        GZIPOutputStream GZIPOutputStream
            = new GZIPOutputStream(byteArrayOutputStream);

        GZIPOutputStream.write(buffer);
        GZIPOutputStream.flush();
        GZIPOutputStream.close();
        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();

        return byteArrayOutputStream;
    }

    private void sendData(byte[] buffer) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = this.compressData(buffer);

        // an ID and a timestamp will be generated
        this.sendMessage(new Message(-1,
            -1,
            new SoundPacket(byteArrayOutputStream.toByteArray())));
    }

    private void sendEmptyPacket() throws IOException {
        this.sendMessage(
                new Message(-1, -1, new SoundPacket(null)));
    }

    private void sendMessage(Message message) throws IOException {
        this.toServer.writeObject(message);
    }
}
