package utils.sentimentAnalysis;

import lombok.Data;
import model.Tweet;
import utils.read.TweetWriter;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

@Data
public class SentimentAnalyzer {

    String algorithm;
    Classifier model;

    public SentimentAnalyzer(String algorithm) {

        this.algorithm = algorithm;

        switch (algorithm) {
            case("ibk"):
                model = new IBk();
                break;
            case("naiveBayes"):
                model = new NaiveBayes();
                break;
            case("smo"):
                model = new SMO();
                break;
            case("j48"):
                model = new J48();
                break;
            default:
                model = new RandomForest();
        }

    }

    public String analyzeTest(Instances train, Instances test) throws Exception {

        model.buildClassifier(train);
        Evaluation eval_train = new Evaluation(test);
        eval_train.evaluateModel(model,test);

        if (Double.isNaN(eval_train.errorRate()))
            return "";
        else {
            return (String.format("\n%s Success Rate: %s - Correct Guess: %s",
                    this.algorithm, eval_train.pctCorrect(), eval_train.correct()));
        }

    }

    public String analyze(Instances train, Instances test, List<Tweet> testTweets, String folder) throws Exception {

        List<Tweet> targetTweets = new ArrayList<>();

        String[] tags = {"pozitif", "negatif", "notr"};
        model.buildClassifier(train);

        System.out.println("TRAIN INSTANCE COUNT: "+train.numInstances());
        System.out.println("TEST INSTANCE COUNT: "+test.numInstances());

        for (int i = 0; i < test.numInstances(); i++) {

            Double tagDouble = model.classifyInstance(test.instance(i));
            String tag = tags[tagDouble.intValue()];
            String content = testTweets.get(i).getContent();
            targetTweets.add(new Tweet(tag, content, 0));
        }

        TweetWriter writer = new TweetWriter(folder);
        writer.writeTargetTweetsToFile(targetTweets);

        return "Target tweets are tagged, check output folder...";
    }

}
