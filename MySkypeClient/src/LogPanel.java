/**
 * Created by Thoma on 04/06/2017.
 */
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 *  Displays STDOUT on a TextArea
 */
public class LogPanel extends JPanel implements Consumer {
    private JTextArea textArea;

    public LogPanel() {
        setLayout(new BorderLayout());
        setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 0), BorderFactory.createTitledBorder("Chat")));
        textArea = new JTextArea(6,32);
        textArea.setEditable(false);
        add(new JScrollPane(textArea),BorderLayout.CENTER);

    }

    @Override
    public void appendText(final String text) {
        if (EventQueue.isDispatchThread()) {
            textArea.append(text);
            textArea.setCaretPosition(textArea.getText().length());
        } else {

            EventQueue.invokeLater(() -> {
                appendText(text);
            });


        }
    }

}

interface Consumer {
    public void appendText(String text);
}