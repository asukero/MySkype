import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class TextHandler extends DataHandler {
    private Scanner scanner;
    public static String DEFAULT_USERNAME = "Anonymous";

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

    private String getDate(long date) {
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(date);
    }

    private String getTextFormatted(String text, long realDate,
        String username) {
        if (username != null && !username.isEmpty()) {
            username = TextHandler.DEFAULT_USERNAME;
        }

        return String.format(
            "[%s] %s: %s", this.getDate(realDate), username, text);
    }

    public String getTextFromConsole() {
        return this.scanner.next();
    }

    public void displayText(String text, long realDate, String username) {
        System.out.println(this.getTextFormatted(text, realDate, username));
    }

    public void displayText(byte[] text, long realDate, String username) {
        this.displayText(this.getStringFromBytes(text), realDate, username);
    }
}
