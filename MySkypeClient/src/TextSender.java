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

    public void sendText(String text) {
        try {
            ByteArrayOutputStream byteArrayOutputStream
                = TextHandler.compressData(text.getBytes());

            // an ID and a timestamp will be generated
            this.sendMessage(new Message(
                -1,
                -1,
                new TextPacket(byteArrayOutputStream.toByteArray())));
        } catch (IOException IOException) {
            System.out.println("Connection error: " + IOException.getMessage());

            this.closeSenderThread();
        }
    }

    @Override
    public void run() {
        while (true) {
            String text = this.textHandler.getTextFromConsole();
            this.sendText(text);
        }
    }
}
