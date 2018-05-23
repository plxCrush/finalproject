import javax.swing.*;

public class Main {

	public static void main(String[] args) {

	    Gui gui = new Gui();
	    JFrame frame = new JFrame("Sentiment Analysis & Artificial Data");
	    frame.setContentPane(gui.getGuiPanel());
	    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    frame.pack();
	    frame.setVisible(true);

	}
}
