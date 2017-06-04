/**
 * Created by Thoma on 04/06/2017.
 */
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 *  StreamLoggger for the LogPanel. Redirects STDOUT to LogPanel.
 */
public class StreamLogger extends OutputStream {
    private StringBuilder buffer;
    private String prefix;
    private Consumer consumer;
    private PrintStream old;

    public StreamLogger(String prefix, Consumer consumer, PrintStream old) {
        this.prefix = prefix;
        buffer = new StringBuilder(128);
        this.old = old;
        this.consumer = consumer;
    }

    @Override
    public void write(int b) throws IOException {
        char c = (char) b;
        String value = Character.toString(c);
        buffer.append(value);
        if (value.equals("\n")) {
            consumer.appendText(buffer.toString());
            buffer.delete(0, buffer.length());
        }
        old.print(c);
    }
}