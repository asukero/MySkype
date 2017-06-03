import java.io.Serializable;

public class TextPacket extends Packet implements Serializable {
    public TextPacket(byte[] data) {
        this.data = data;
    }
}
