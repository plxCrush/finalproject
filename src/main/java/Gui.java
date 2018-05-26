import com.graphbuilder.struc.Bag;
import lombok.Data;
import model.Tweet;
import utils.bag.BagUtils;
import utils.read.ArffUtils;
import utils.read.TweetGroupInfo;
import utils.read.TweetReader;
import utils.sentimentAnalysis.SentimentAnalyzer;
import weka.core.Instances;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class Gui {

    public String inputFilePath = "data/3969tweets.xls";
    public String targetFilePath = "";

    public static final String OUTPUT_FOLDER = "output/";
    public static final String DATA_FOLDER = "data/";
    public static final String trainFileName = OUTPUT_FOLDER+"train.arff";
    public static final String testFileName = OUTPUT_FOLDER+"test.arff";

    public int readTweetsStartPoint = 1;
    public int readTweetsAmount = 1000;

    public String algorithmSelector;

    public List<Tweet> trainTweets;
    public List<Tweet> testTweets;

    public Bag bag;

    public Gui() {

        // SETTING DEFAULT VALUES

        minWordOccurField.setText("5");
        minTfIdfField.setText("0");
        maxTfIdfField.setText("0");

        readTweetsStartPointField.setText("1");
        readTweetsAmountField.setText("1000");

        randomForestRadioButton.setSelected(true);
        useWordSuggestionRadioButton.setSelected(true);
        algorithmSelector = "randomForest";

        splitTweetsRadioButton.setSelected(true);

        // BUTTON ACTION LISTENERS

        browseInputFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                int returnValue = jfc.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = jfc.getSelectedFile();
                    inputFilePath = selectedFile.getAbsolutePath();
                    getInputFileLabel().setText(selectedFile.getName());
                }
            }
        });

        browseTargetFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                int returnValue = jfc.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = jfc.getSelectedFile();
                    targetFilePath = selectedFile.getAbsolutePath();
                    getOutputFileLabel().setText(selectedFile.getName());
                }
            }
        });

        readTweetsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                consoleField.setText("Reading tweets from input files...");

                TweetReader trainTweetReader = new TweetReader(inputFilePath,
                        Integer.parseInt(readTweetsStartPointField.getText()),
                        Integer.parseInt(readTweetsAmountField.getText()));

                List<Tweet> allTweets = new ArrayList<>();

                try {
                    allTweets = trainTweetReader.read();
                    consoleField.setText(String.format("Read %s input tweets\n", allTweets.size()));

                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                if (splitTweetsRadioButton.isSelected()) {

                    Tweet[][] splitted = trainTweetReader.split(allTweets);
                    trainTweets = Arrays.asList(splitted[0]);
                    testTweets = Arrays.asList(splitted[1]);
                }
                else {

                    trainTweets = allTweets;

                    if (targetFilePath.equals("")) {

                        TweetReader targetTweetReader = new TweetReader();
                        testTweets = targetTweetReader.readFromField(getSingleTweetInputField().getText());

                    }
                    else {

                        // 0 means go through all rows for tweet reader
                        TweetReader targetTweetReader = new TweetReader(targetFilePath, 1, 0);

                        try {
                            testTweets = targetTweetReader.read();
                            consoleField.append(String.format("Read %s target tweets", testTweets.size()));

                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                    }
                }

                TweetGroupInfo info = new TweetGroupInfo();

                consoleField.append(String.format("\n%s train tweets", trainTweets.size()));
                consoleField.append(info.sentimentDistrubiton(trainTweets));
                consoleField.append(String.format("\n%s test tweets", testTweets.size()));

                if(splitTweetsRadioButton.isSelected()) {
                    consoleField.append(info.sentimentDistrubiton(testTweets));
                }

                consoleField.append("\n");

            }
        });

        createBagButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                BagUtils bagUtils = new BagUtils(Integer.parseInt(minWordOccurField.getText()),
                        Integer.parseInt(minTfIdfField.getText()),
                        Integer.parseInt(maxTfIdfField.getText()),
                        useWordSuggestionRadioButton.isSelected());

                consoleField.append("\nFiltering tweet words by your settings...");
                bagUtils.reduceTweetWords(trainTweets, testTweets);
                consoleField.append("\nCreating bag...");
                bag = bagUtils.create(trainTweets, testTweets);
                consoleField.append(String.format("\nBag is created with size of %s\n",bag.size()));

                consoleField.append("\nGenerating bag of words for each tweet...");
                for (Tweet t: trainTweets) {
                    t.generateBow(bag);
                }
                for (Tweet t: testTweets) {
                    t.generateBow(bag);
                }

                for (Tweet t: testTweets) {
                    t.print();
                }

                consoleField.append("\nBag of words generated...\n");

            }
        });

        createAndAddArtificialButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("CREATE AND ADD ARTIFICIAL DATA");
            }
        });

        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                consoleField.append("\nWriting Arff files to output path...");
                ArffUtils arffUtils = new ArffUtils();
                try {
                    arffUtils.write(bag, trainTweets, trainFileName, true);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    arffUtils.write(bag, testTweets, testFileName, true);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                Instances trainInstances = null;
                Instances testInstances = null;

                try {
                    trainInstances = arffUtils.getInstances(trainFileName);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                try {
                    testInstances = arffUtils.getInstances(testFileName);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                consoleField.append("\nArff files are ready...");

                consoleField.append("\nSentiment Analysis running...");
                SentimentAnalyzer analyzer = new SentimentAnalyzer(algorithmSelector);
                try {
                    String info = analyzer.anaylze(trainInstances, testInstances, true);
                    consoleField.append(String.format("\n%s\n",info));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        // ALGORITHM SELECT RADIO BUTTON ACTION LISTENERS

        final ButtonGroup group = new ButtonGroup();
        group.add(naiveBayesRadioButton);
        group.add(j48RadioButton);
        group.add(SMORadioButton);
        group.add(randomForestRadioButton);
        group.add(IBKRadioButton);

        naiveBayesRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                algorithmSelector = "naiveBayes";
            }
        });

        j48RadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                algorithmSelector = "j48";
            }
        });

        SMORadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                algorithmSelector = "smo";
            }
        });

        randomForestRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                algorithmSelector = "randomForest";
            }
        });

        IBKRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                algorithmSelector = "ibk";
            }
        });

    }

    private JPanel MainPanel;
    private JRadioButton IBKRadioButton;
    private JRadioButton naiveBayesRadioButton;
    private JRadioButton randomForestRadioButton;
    private JRadioButton j48RadioButton;
    private JRadioButton SMORadioButton;
    private JPanel AlgorithmPanel;
    private JPanel ConsolePanel;
    private JTextArea consoleField;
    private JTextArea SingleTweetInputField;
    private JButton singleTweetSendButton;
    private JPanel FileSelectorPanel;
    private JButton browseInputFileButton;
    private JButton browseTargetFileButton;
    private JLabel InputFileLabel;
    private JLabel OutputFileLabel;
    private JPanel ActionsPanel;
    private JButton createBagButton;
    private JButton runButton;
    private JButton createAndAddArtificialButton;
    private JPanel SettingsPanel;
    private JTextField minWordOccurField;
    private JTextField minTfIdfField;
    private JButton readTweetsButton;
    private JTextField readTweetsStartPointField;
    private JTextField readTweetsAmountField;
    private JRadioButton splitTweetsRadioButton;
    private JTextField maxTfIdfField;
    private JRadioButton useWordSuggestionRadioButton;

    public static void main(String[] args) {

        JFrame frame = new JFrame("Sentiment Analysis & Artificial Data");
        frame.setContentPane(new Gui().getMainPanel());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1200, 800);
        frame.setVisible(true);

    }

}
