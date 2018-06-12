import com.graphbuilder.struc.Bag;
import lombok.Data;
import model.Tweet;
import utils.artificialData.DataCreator;
import utils.artificialData.TranslationDataCreator;
import utils.artificialData.VectoralDataCreator;
import utils.bag.BagUtils;
import utils.read.ArffUtils;
import utils.read.TweetGroupInfo;
import utils.read.TweetReader;
import utils.read.WordSimilarReader;
import utils.sentimentAnalysis.SentimentAnalyzer;
import utils.wordSuggestion.WordCorrector;
import weka.core.Instances;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Data
public class Gui {

    public String inputFilePath = "data/3000+3000TaggedTranslatedForTestNEW.xls";
    public String targetFilePath = "";

    public static final String OUTPUT_FOLDER = "output/";
    public static final String DATA_FOLDER = "data/";
    public static final String trainFileName = OUTPUT_FOLDER+"train.arff";
    public static final String testFileName = OUTPUT_FOLDER+"test.arff";
    public static final String wordSimilaritiesFile = DATA_FOLDER+"30022wordSimilarities.txt";
    public static final String createdTweetsFile = OUTPUT_FOLDER+"createdTweets.txt";

    public String algorithmSelector;
    public String tweetRepresentationInBag;
    public String dataCreationMethod;

    public List<Tweet> allInputTweets;
    public List<Tweet> trainTweets;
    public List<Tweet> testTweets;
    public List<Tweet> createdTweets;
    public List<Tweet> translatedTweets;

    public Bag bag;

    public Gui() {

        // SETTING DEFAULT VALUES

        // read options
        readTweetsStartPointField.setText("1");
        readTweetsAmountField.setText("0");

        splitTweetsRadioButton.setSelected(true);
        splitTrainPercentageField.setText("50");

        containsTranslatedDataRadioButton.setSelected(true);

        // bag options
        minWordOccurField.setText("5");
        minTfIdfField.setText("0");
        maxTfIdfField.setText("0");

        useTfRadioButton.setSelected(true);
        tweetRepresentationInBag = "tf";

        // algorithm options
        randomForestRadioButton.setSelected(true);
        useWordSuggestionRadioButton.setSelected(true);
        algorithmSelector = "randomForest";

        //artificial data options
        useVectoralDataCreationRadioButton.setSelected(true);
        dataCreationMethod = "vector";
        upperLimitArtificialField.setText("0");
        upperLimitForOneTweetField.setText("1");

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
                allInputTweets = new ArrayList<>();
                translatedTweets = new ArrayList<>();

                try {
                    allInputTweets = trainTweetReader.read();
                    consoleField.setText(String.format("Read %s input tweets\n", allInputTweets.size()));

                    if (containsTranslatedDataRadioButton.isSelected()) {

                        Tweet[][] splitted = trainTweetReader.split(allInputTweets, 50, false);
                        allTweets = new ArrayList<>(Arrays.asList(splitted[0]));
                        translatedTweets = new ArrayList<>(Arrays.asList(splitted[1]));

//                        for (Tweet t: allTweets)
//                            t.print();
//
//                        System.out.println("translated \n \n");
//
//                        for (Tweet t: translatedTweets)
//                            t.print();
                    }

                    else{

                        allTweets = allInputTweets;
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                if (splitTweetsRadioButton.isSelected()) {

                    Tweet[][] splitted = trainTweetReader.split(allTweets,
                            Integer.parseInt(splitTrainPercentageField.getText()),
                            true);
                    trainTweets = new ArrayList<>(Arrays.asList(splitted[0]));
                    testTweets = new ArrayList<>(Arrays.asList(splitted[1]));
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

                if (useWordSuggestionRadioButton.isSelected()) {

                    try {
                        WordCorrector wordCorrector = new WordCorrector();
                        wordCorrector.correctWords(trainTweets);
                        wordCorrector.correctWords(testTweets);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

                TweetGroupInfo info = new TweetGroupInfo();

                consoleField.append(String.format("\n%s train tweets", trainTweets.size()));
                consoleField.append(info.sentimentDistrubiton(trainTweets));
                consoleField.append(String.format("\n\n%s test tweets", testTweets.size()));
                consoleField.append(info.sentimentDistrubiton(testTweets));
                consoleField.append(String.format("\n\n%s translated tweets", translatedTweets.size()));
//                consoleField.append(info.sentimentDistrubiton(translatedTweets));

                consoleField.append("\n");
            }
        });

        createBagButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                BagUtils bagUtils = new BagUtils(Integer.parseInt(minWordOccurField.getText()),
                        Double.parseDouble(minTfIdfField.getText()),
                        Double.parseDouble(maxTfIdfField.getText()));

                consoleField.append("\nFiltering tweet words by your settings...");
                bagUtils.reduceTweetWords(trainTweets, testTweets);
                consoleField.append("\nCreating bag...");
                bag = bagUtils.create(trainTweets, testTweets);
                consoleField.append(String.format("\nBag is created with size of %s\n",bag.size()));

                consoleField.append("\nGenerating bag of words for each tweet...");

                List<List<String>> allTweets = new ArrayList<>();

                for (Tweet t : trainTweets) {
                    allTweets.add(t.getWords());
                }

                for (Tweet t : testTweets) {
                    allTweets.add(t.getWords());
                }

                for (Tweet t: trainTweets) {
                    t.generateBow(bag, allTweets, tweetRepresentationInBag);
                }
                for (Tweet t: testTweets) {
                    t.generateBow(bag, allTweets, tweetRepresentationInBag);
                }

                consoleField.append("\nBag of words generated...\n");
            }
        });

        createAndAddArtificialButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                consoleField.append("\nCreating tweets...\n");

                if(createdTweets != null && createdTweets.size() > 0)
                    trainTweets.removeAll(createdTweets);

                DataCreator creator;

                if (dataCreationMethod.equals("vector")) {

                    WordSimilarReader reader = new WordSimilarReader(wordSimilaritiesFile);
                    HashMap<String, String> similarities = reader.getWordSimilarities();

                    creator = new VectoralDataCreator(
                            Integer.parseInt(upperLimitArtificialField.getText()),
                            Integer.parseInt(upperLimitForOneTweetField.getText()),
                            createdTweetsFile,
                            similarities);
                }

                else {

                    creator = new TranslationDataCreator(allInputTweets,
                            translatedTweets.size(),
                            createdTweetsFile);
                }

                createdTweets = new ArrayList<>();
                try {
                    createdTweets = creator.create(trainTweets);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                trainTweets.addAll(createdTweets);

                consoleField.append(String.format("\nCreated %s tweets.\n",createdTweets.size()));
                consoleField.append(String.format("\nNew amount of train tweets: %s\n",trainTweets.size()));
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
                    String info = analyzer.analyzeTest(trainInstances, testInstances);
                    consoleField.append(String.format("\n%s\n",info));
                    info = analyzer.analyze(trainInstances, testInstances, testTweets, OUTPUT_FOLDER);
                    consoleField.append(String.format("\n%s\n",info));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        openOutputFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().open(new File(OUTPUT_FOLDER));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        clearCreatedDataFromButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(createdTweets != null && createdTweets.size() > 0) {
                    trainTweets.removeAll(createdTweets);
                }

                consoleField.append(String.format("\nNew amount of train tweets: %s\n",trainTweets.size()));
            }
        });

        resetWordsOfTweetsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                for (Tweet t: trainTweets) {
                    t.resetWords();
                }
                for (Tweet t: testTweets) {
                    t.resetWords();
                }

                if (useWordSuggestionRadioButton.isSelected()) {

                    try {
                        WordCorrector wordCorrector = new WordCorrector();
                        wordCorrector.correctWords(trainTweets);
                        wordCorrector.correctWords(testTweets);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

                consoleField.append("\nTweet words were reset...");
            }
        });

        // DATA CREATION RADIO BUTTON ACTION LISTENERS
        final ButtonGroup group3 = new ButtonGroup();
        group3.add(useVectoralDataCreationRadioButton);
        group3.add(useTranslationDataCreationRadioButton);

        useTranslationDataCreationRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dataCreationMethod = "translation";
            }
        });
        useVectoralDataCreationRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dataCreationMethod = "vector";
            }
        });

        // TF-IDF REPRESENTATION RADIO BUTTON ACTION LISTENERS

        final ButtonGroup group2 = new ButtonGroup();
        group2.add(useTfRadioButton);
        group2.add(useTfIdfRadioButton);

        useTfIdfRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                tweetRepresentationInBag = "tfIdf";
            }
        });
        useTfRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                tweetRepresentationInBag = "tf";
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
    private JButton openOutputFolderButton;
    private JTextField upperLimitArtificialField;
    private JTextField upperLimitForOneTweetField;
    private JRadioButton useTfRadioButton;
    private JRadioButton useTfIdfRadioButton;
    private JTextField splitTrainPercentageField;
    private JRadioButton useVectoralDataCreationRadioButton;
    private JRadioButton useTranslationDataCreationRadioButton;
    private JRadioButton containsTranslatedDataRadioButton;
    private JButton clearCreatedDataFromButton;
    private JButton resetWordsOfTweetsButton;

    public static void main(String[] args) {

        JFrame frame = new JFrame("Sentiment Analysis & Artificial Data");
        Gui gui = new Gui();
        frame.setContentPane(gui.getMainPanel());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1200, 800);
        frame.setVisible(true);

    }

}
