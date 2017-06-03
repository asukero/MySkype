import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class TextSender extends ObjectSender {
    private TextHandler textHandler = new TextHandler();
    public TextSender(ObjectOutputStream toServer) {
        this.toServer = toServer;
    }

    @Override
    protected ByteArrayOutputStream compressData(byte[] buffer)
            throws IOException {
        return TextHandler.compressData(buffer);
    }

    @Override
    protected Packet createPacket(ByteArrayOutputStream byteArrayOutputStream) {
        return new TextPacket(byteArrayOutputStream.toByteArray());
    }

    @Override
    protected void sendEmptyPacket() throws IOException {
        this.sendMessage(
            new Message(-1, -1, new TextPacket(null)));
    }

    @Override
    public void run() {
        try {
            while (true) {
                String text = this.textHandler.getTextFromConsole();
                ByteArrayOutputStream byteArrayOutputStream
                    = TextHandler.compressData(text.getBytes());

                // an ID and a timestamp will be generated
                this.sendMessage(new Message(
                    -1,
                    -1,
                    new TextPacket(byteArrayOutputStream.toByteArray())));
            }
        } catch (Exception exception) {
            System.out.println("Connection error: " + exception.getMessage());

            this.closeSenderThread();
        }
    }
}
