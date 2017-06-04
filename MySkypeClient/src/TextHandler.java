import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class TextHandler extends DataHandler {
    private Scanner scanner;

    public TextHandler() {
        this.scanner = new Scanner(System.in);
    }

    private String getStringFromBytes(byte[] bytes) {
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private String getTextFormatted(String text, long timestamp) {
        return "[" + timestamp + "] Anonymous: " + text;
    }

    public String getTextFromConsole() {
        return this.scanner.next();
    }

    public void displayText(String text, long timestamp) {
        System.out.println(this.getTextFormatted(text, timestamp));
    }

    public void displayText(byte[] text, long timestamp) {
        this.displayText(this.getStringFromBytes(text), timestamp);
    }
}
