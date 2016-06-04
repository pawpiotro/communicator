/**
 * Strumień niestandardowy. Dzięki niemu wszystkie komunikaty i błędy programu wypisywane są w oknie
 * Informacje te są potrzebne do nawiązywania połączeń między klientami.
 */

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

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
