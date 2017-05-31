import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

public class AudioChannel extends Thread {
    private Integer ID;
    private ArrayList<Message> messagesToPlay = new ArrayList<>();
    private int lastSoundPacketLen = SoundPacket.defaultDataLength;
    private long lastPacketTime = System.nanoTime();
    private SourceDataLine speaker = null;

    public boolean canKill() {
        // it's been a long time (5s) since the last received packet...
        return System.nanoTime() - this.lastPacketTime > 5000000000L;
    }

    public void closeAndKill() {
        if (this.speaker != null) this.speaker.close();

        this.stopAudioChannel();
    }

    private void stopAudioChannel() {
        try {
            this.join();
        } catch (InterruptedException interruptedException) {
            System.out.print("Join error: "
                + interruptedException.getMessage());
        }
    }

    public AudioChannel(Integer ID) {
        this.ID = ID;
    }

    public Integer getID() {
        return this.ID;
    }

    public void addMessageToPlay(Message message) {
        this.messagesToPlay.add(message);
    }

    private void startSoundChannel() {
        try {
            AudioFormat audioFormat = SoundPacket.defaultFormat;
            DataLine.Info info
                = new DataLine.Info(SourceDataLine.class, audioFormat);

            this.speaker = (SourceDataLine) AudioSystem.getLine(info);
            this.speaker.open(audioFormat);
            this.speaker.start();
        } catch (LineUnavailableException lineUnavailableException) {
            System.out.println("Sound card error: "
                + lineUnavailableException.getMessage());
            this.closeAndKill();
        }
    }

    private void playComfortNoise() {
        byte[] noise = new byte[this.lastSoundPacketLen];

        for (int i = 0; i < noise.length; i++) {
            noise[i] = (byte) ((Math.random() * 3) - 1);
        }

        this.speaker.write(noise, 0, noise.length);
    }

    private byte[] decompressData(byte[] data) {
        try {
            GZIPInputStream gzipInputStream
                = new GZIPInputStream(new ByteArrayInputStream(data));
            ByteArrayOutputStream byteArrayOutputStream
                = new ByteArrayOutputStream();

            for (;;) {
                int byteRead = gzipInputStream.read();

                if (byteRead == -1) {
                    break;
                }

                byteArrayOutputStream.write((byte) byteRead);
            }

            return byteArrayOutputStream.toByteArray();
        } catch (IOException IOException) {
            System.out.print("Decompress error: " + IOException.getMessage());

            return null;
        }
    }

    private void playData(byte[] soundToPlay, SoundPacket soundPacket) {
        this.speaker.write(soundToPlay, 0, soundToPlay.length);
        this.lastSoundPacketLen = soundPacket.getData().length;
    }

    private void handleData(Message message) {
        SoundPacket soundPacket = (SoundPacket) (message.getData());

        if (soundPacket == null) return;

        if (soundPacket.getData() == null) {
            this.playComfortNoise(); // sender skipped a packet

            return;
        }

        this.playData(this.decompressData(soundPacket.getData()),
                soundPacket);
    }

    @Override
    public void run() {
        try {
            this.startSoundChannel();

            // check for new messages to be played
            while(true) {
                if (this.messagesToPlay.isEmpty()) {
                    Utils.sleep(10);

                    continue;
                }

                // message received
                this.lastPacketTime = System.nanoTime();
                Message message = this.messagesToPlay.remove(0);

                if (!(message.getData() instanceof SoundPacket)) {
                    continue; // invalid message
                }

                this.handleData(message);
            }
        } catch (Exception exception) {
            System.out.println("Connection error: " + exception.getMessage());

            this.closeAndKill();
        }
    }
}
