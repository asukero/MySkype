import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Username implements Serializable {
    public static String DEFAULT_USERNAME = "Anonymous";
    public static String DEFAULT_HOSTNAME = "Unknown";

    private String hostname = null;
    private String username = "Anonymous";

    public Username(String username) {
        if (username == null || username.isEmpty()) {
            username = Username.DEFAULT_USERNAME;
        }

        this.username = username;
        this.getHostname();
    }

    @Override
    public String toString() {
        return String.format("%s@%s", this.username, this.hostname);
    }

    private void getHostname() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            this.hostname = inetAddress.getHostName();
        } catch (UnknownHostException ex) {
            this.hostname = Username.DEFAULT_HOSTNAME;
            Utils.displayError("Client error: hostname can not be resolved");
        }
    }
}
