package MVC;

import java.io.OutputStream;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class TextAreaOutputStream extends OutputStream {
    private JTextArea textArea;
    private StringBuilder sb = new StringBuilder();

    public TextAreaOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) {
        char c = (char) b;
        sb.append(c);
        if (c == '\n') {
            final String text = sb.toString();
            SwingUtilities.invokeLater(() -> textArea.append(text));
            sb.setLength(0);
        }
    }
}
