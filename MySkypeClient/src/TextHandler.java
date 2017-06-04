import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    private String getDate(long timestamp) {
        Date date = new Date(timestamp);
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(date);
    }

    private String getTextFormatted(String text, long realDate) {
        return "[" + this.getDate(realDate) + "] Anonymous: " + text;
    }

    public String getTextFromConsole() {
        return this.scanner.next();
    }

    public void displayText(String text, long realDate) {
        System.out.println(this.getTextFormatted(text, realDate));
    }

    public void displayText(byte[] text, long realDate) {
        this.displayText(this.getStringFromBytes(text), realDate);
    }
}
