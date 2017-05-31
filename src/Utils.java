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

    public static String getExternalIP() {
        try {
            URL myIp = new URL("http://checkip.dyndns.org/");

            BufferedReader in =
                new BufferedReader(
                new InputStreamReader(myIp.openStream()));

            String s = in.readLine();

            return s.substring(s.lastIndexOf(":") + 2, s.lastIndexOf
                    ("</body>"));
        } catch (Exception exception) {
            return "error " + exception;
        }
    }

    public static String getInternalIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            return "error";
        }
    }


    public static void displayError(String message) {
        JOptionPane.showMessageDialog(null, message);
    }
}
