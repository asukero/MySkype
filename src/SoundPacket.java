import javax.sound.sampled.AudioFormat;
import java.io.Serializable;

public class SoundPacket extends Packet implements Serializable {
    // 11.025khz, 8bit, mono, signed...
    // ... and big endian (changes  nothing in 8 bit) ~8kb/s
    public static AudioFormat defaultFormat = new AudioFormat(11025f, 8, 1,
        true, true);
    // if this.data is null, comfort noise will be played

    public SoundPacket(byte[] data) {
        this.data = data;
    }
}
