import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

public class Utils {
    public static void sleep(int msTime) {
        try {
            Thread.sleep(msTime);
        } catch (InterruptedException exception) {
            Utils.displayError("Sleep error: " + exception.getMessage());
        }
    }

    public static void displayError(String message) {
        Utils.displayError(message, null);
    }

    public static void displayError(String message, JRootPane JRootPane) {
        JOptionPane.showMessageDialog(JRootPane,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
