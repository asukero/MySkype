import javax.sound.sampled.LineUnavailableException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SoundSender extends Microphone {
    public static double amplification = 1.0d;

    public SoundSender(ObjectOutputStream toServer, Username username)
        throws LineUnavailableException {
        this.toServer = toServer;
        this.openMicrophoneLine();
        this.username = username;
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
                System.out.println("Connection error: "
                    + IOException.getMessage());

                this.closeSenderThread();
            }
        }
    }

    @Override
    protected ByteArrayOutputStream compressData(byte[] buffer)
        throws IOException {
        return SoundHandler.compressData(buffer);
    }

    @Override
    protected Packet createPacket(ByteArrayOutputStream byteArrayOutputStream) {
        return new SoundPacket(byteArrayOutputStream.toByteArray());
    }

    @Override
    protected void sendEmptyPacket() throws IOException {
        this.sendMessage(
            new Message(-1, -1, new SoundPacket(null),
            this.username));
    }
}
