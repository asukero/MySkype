import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public abstract class ObjectSender extends Thread {
    protected ObjectOutputStream toServer;

    protected void sendData(byte[] buffer) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream
                = this.compressData(buffer);

        // an ID and a timestamp will be generated
        this.sendMessage(new Message(-1,
                -1,
                this.createPacket(byteArrayOutputStream)));
    }

    protected abstract ByteArrayOutputStream compressData(byte[] buffer)
        throws IOException;

    protected abstract Packet createPacket(
        ByteArrayOutputStream byteArrayOutputStream);

    protected abstract void sendEmptyPacket() throws IOException;

    protected void sendMessage(Message message) throws IOException {
        this.toServer.writeObject(message);
    }

    protected void closeSenderThread() {
        try {
            this.join();
        } catch (InterruptedException interruptedException) {
            System.out.println("InterruptedException error: "
                + interruptedException.getMessage());
        }
    }
}
