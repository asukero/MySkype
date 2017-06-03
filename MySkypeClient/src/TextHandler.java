import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class TextHandler extends DataHandler {
    private Scanner scanner;

    public TextHandler() {
        this.scanner = new Scanner(System.in);
    }

    public String getTextFromGUI() {
        System.out.println("Warning: getTextFromGUI not implemented!");

        return null;
    }

    private String getStringFromBytes(byte[] bytes) {
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private String getTextFormatted(String text, long timestamp) {
        return "Anonymous [" + timestamp + "]: " + text;
    }

    public String getTextFromConsole() {
        return this.scanner.next();
    }

    public void displayOnGUI(String text, long timestamp) {
        System.out.println("Warning: displayOnGUI not implemented!");
        // TODO: print this.getTextFormatted(text, timestamp)
    }

    public void displayOnConsole(String text, long timestamp) {
        System.out.println(this.getTextFormatted(text, timestamp));
    }

    public void displayOnGui(byte[] text, long timestamp) {
        this.displayOnGUI(this.getStringFromBytes(text), timestamp);
    }

    public void displayOnConsole(byte[] text, long timestamp) {
        this.displayOnConsole(this.getStringFromBytes(text), timestamp);
    }
}
