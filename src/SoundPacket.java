import javax.sound.sampled.AudioFormat;
import java.io.Serializable;

public class SoundPacket implements Serializable {
    // 11.025khz, 8bit, mono, signed...
    // ... and big endian (changes  nothing in 8 bit) ~8kb/s
    public static AudioFormat defaultFormat = new AudioFormat(11025f, 8, 1,
        true, true);
    public static int defaultDataLength = 1200; // send 1200 samples/packet
    private byte[] data; // if null, comfort noise will be played

    public SoundPacket(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return this.data;
    }
}
