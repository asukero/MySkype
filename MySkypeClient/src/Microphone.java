import javax.sound.sampled.*;

public class Microphone extends Thread {
    protected TargetDataLine microphone;

    protected void openMicrophoneLine() throws LineUnavailableException {
        AudioFormat audioFormat = SoundPacket.defaultFormat;
        DataLine.Info info
                = new DataLine.Info(TargetDataLine.class, null);
        this.microphone = (TargetDataLine) AudioSystem.getLine(info);
        this.microphone.open(audioFormat);
        this.microphone.start();
    }

    protected long getBufferCount(byte[] buffer) {
        long bufferCount = 0;

        for (int i = 0; i < buffer.length; i++) {
            buffer[i] *= MicrophoneThread.amplification;
            bufferCount += Math.abs(buffer[i]);
        }

        bufferCount *= 2.5;
        bufferCount /= buffer.length;

        return bufferCount;
    }

    protected byte[] readFromMicrophone() {
        byte[] buffer = new byte[SoundPacket.defaultDataLength];

        // flush old data from microphone to reduce lag
        while (this.microphone.available() >= SoundPacket.defaultDataLength) {
            this.microphone.read(buffer, 0, buffer.length);
        }

        return buffer;
    }
}
