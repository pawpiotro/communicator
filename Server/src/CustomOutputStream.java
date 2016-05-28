/**
 * Strumień niestandardowy. Dzięki niemu wszystkie komunikaty i błędy programu wypisywane są w oknie
 * Informacje te są potrzebne do nawiązywania połączeń między klientami.
 */

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;

public class CustomOutputStream extends OutputStream {
    private JTextArea textArea;

    public CustomOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
        textArea.append(String.valueOf((char)b));
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}
