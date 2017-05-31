import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;

public class MicThread extends Thread {
    public static double amplification = 1.0d;
    private ObjectOutputStream toServer;
    private TargetDataLine microphone;

    public MicThread(ObjectOutputStream toServer)
        throws LineUnavailableException {
        this.toServer = toServer;
        this.openMicrophoneLine();
    }

    private void openMicrophoneLine() throws LineUnavailableException {
        AudioFormat audioFormat = SoundPacket.defaultFormat;
        DataLine.Info info
            = new DataLine.Info(TargetDataLine.class, null);
        this.microphone = (TargetDataLine) AudioSystem.getLine(info);
        this.microphone.open(audioFormat);
        this.microphone.start();
    }

    private byte[] readFromMicrophone() {
        byte[] buffer = new byte[SoundPacket.defaultDataLength];

        // flush old data from microphone to reduce lag
        while (this.microphone.available() >= SoundPacket.defaultDataLength) {
            this.microphone.read(buffer, 0, buffer.length);
        }

        return buffer;
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
                long bufferCount = 0;

                for (int i = 0; i < buffer.length; i++) {
                    buffer[i] *= MicThread.amplification;
                    bufferCount += Math.abs(buffer[i]);
                }

                bufferCount *= 2.5;
                bufferCount /= buffer.length;

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
