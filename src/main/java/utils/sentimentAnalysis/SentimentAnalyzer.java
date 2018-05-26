package utils.sentimentAnalysis;

import lombok.AllArgsConstructor;
import lombok.Data;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

@Data
@AllArgsConstructor
public class SentimentAnalyzer {

    String algorithm;

    public String anaylzeTest(Instances train, Instances test) throws Exception {

        Classifier model;

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

        model.buildClassifier(train);
        Evaluation eval_train = new Evaluation(test);
        eval_train.evaluateModel(model,test);

        return (String.format("%s Error Rate: %s - Correct Guess: %s",
                this.algorithm, eval_train.errorRate(), eval_train.correct()));

    }

}
