import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Strumień niestandardowy. Dzięki niemu wszystkie komunikaty i błędy wypisywane są w oknie programu.
 */
public class CustomOutputStream extends OutputStream {
    private JTextArea textArea;

    public CustomOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                textArea.append(String.valueOf((char) b));
                textArea.setCaretPosition(textArea.getDocument().getLength());
            }
        });
    }
}
