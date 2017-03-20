import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class GUI {

    public static void main(String[] args) throws IOException {
	JEditorPane website = new JEditorPane("https://www.google.ca/");
	website.setEditable(false);
	JFrame frame = new JFrame("Google");
	frame.add(new JScrollPane(website));
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setSize(800, 600);
	frame.setVisible(true);
    }
}