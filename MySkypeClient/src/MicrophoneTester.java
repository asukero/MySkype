import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.*;

class MicTester extends Thread {
    private TargetDataLine microphone = null;
    private GUI frame;

    public MicTester(GUI frame) {
        this.frame = frame;
    }

    @Override
    public void run() {
        try {
            AudioFormat audioFormat = SoundPacket.defaultFormat;
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, null);
            microphone = (TargetDataLine) (AudioSystem.getLine(info));
            microphone.open(audioFormat);
            microphone.start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame.getRootPane(), "Microphone not detected.\nPress OK to close this program", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        while (true) {
            Utils.sleep(10);
            if (microphone.available() > 0) {
                byte[] buff = new byte[SoundPacket.defaultDataLength];
                microphone.read(buff, 0, buff.length);
                long tot = 0;

                for (int i = 0; i < buff.length; i++) {
                    tot += MicrophoneThread.amplification * Math.abs(buff[i]);
                }
                tot *= 2.5;
                tot /= buff.length;
                frame.getMicLev().setValue((int) tot);
            }
        }
    }

    public void close() {
        if (microphone != null) microphone.close();
        try {
            join();
        } catch (InterruptedException ex) {
            JOptionPane.showMessageDialog(frame.getRootPane(), "Error while shutting down microphone.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

    }
}