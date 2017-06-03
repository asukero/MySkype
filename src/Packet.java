import java.io.Serializable;

public abstract class Packet implements Serializable {
    public static int defaultDataLength = 1200; // send 1200 samples/packet

    protected byte[] data;

    public byte[] getData() {
        return this.data;
    }
}
