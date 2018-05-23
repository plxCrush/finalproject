import lombok.Data;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Data
public class Gui {

    public Gui() {


        browseInputFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("BROWSE INPUT");
            }
        });
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("Sentiment Analysis & Artificial Data");
        frame.setContentPane(new Gui().getMainPanel());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1600, 900);
        frame.setVisible(true);

    }

    private JPanel MainPanel;
    private JRadioButton IBKRadioButton;
    private JRadioButton naiveBayesRadioButton;
    private JRadioButton randomForestRadioButton;
    private JRadioButton j48RadioButton;
    private JRadioButton SMORadioButton;
    private JPanel AlgorithmPanel;
    private JPanel ConsolePanel;
    private JTextArea Console;
    private JTextArea SingleTweetInput;
    private JButton SingleTweetSendButton;
    private JPanel FileSelectorPanel;
    private JButton browseInputFileButton;
    private JButton browseOutputFileButton;
    private JLabel InputFileLabel;
    private JLabel OutputFileLabel;
    private JPanel ActionsPanel;
    private JButton createBagButton;
    private JButton runButton;
    private JButton createAndAddArtificialButton;
    private JPanel SettingsPanel;
    private JTextField minWordOccurance;
    private JTextField minTfIdf;

}
