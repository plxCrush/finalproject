package utils.bag;

import com.graphbuilder.struc.Bag;
import lombok.AllArgsConstructor;
import lombok.Data;
import model.Tweet;
import utils.wordSuggestion.WordCorrector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
@AllArgsConstructor
public class BagUtils {

    private int minWordOccur;
    private int minTfIdf;
    private int maxTfIdf;
    private boolean useWordSuggestion;

    public Bag create(List<Tweet> trainTweets, List<Tweet> testTweets) {

        Bag bag = new Bag();

        for (Tweet tweet: trainTweets) {
            for(String word : tweet.getWords()) {
                if (!bag.contains(word))
                    bag.add(word);
            }
        }

        for (Tweet tweet: testTweets) {
            for(String word : tweet.getWords()) {
                if (!bag.contains(word))
                    bag.add(word);
            }
        }

        return bag;
    }

    public void reduceTweetWords(List<Tweet> trainTweets, List<Tweet> testTweets) {

        List<List<String>> allTweets = new ArrayList<>();

        for (Tweet t : trainTweets) {
            allTweets.add(t.getWords());
        }

        if (testTweets != null) {

            for (Tweet t : testTweets) {
                allTweets.add(t.getWords());
            }
            reduce(testTweets, allTweets);

            if (this.useWordSuggestion) {
                correctWords(testTweets);
            }
        }

        reduce(trainTweets, allTweets);

        if (this.useWordSuggestion) {
            correctWords(trainTweets);
        }

    }

    private void correctWords(List<Tweet> tweets) {

        try {
            WordCorrector wc = new WordCorrector();
            for (Tweet t : tweets) {
                for (String word : t.getWords()) {
                    if (!wc.checkWord(word)) {
                        List <String> tmp = wc.suggestWords(word);
                        if(tmp.size() > 0) {
                            String newWord = tmp.get(0);
                            t.changeWord(word, newWord);
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reduce(List<Tweet> tweets, List<List<String>> allTweets) {

        TfIdfCalculator tfIdfCalculator = new TfIdfCalculator();

        for (Tweet t : tweets) {

            Iterator<String> iterator = t.getWords().iterator();
            while (iterator.hasNext()) {

                boolean removed = false;

                String tmp = iterator.next();
                double tfIdfValue = tfIdfCalculator.tfIdf(t.getWords(), allTweets, tmp);

                if (minWordOccur > 0) {
                    if (tfIdfCalculator.isRare(allTweets, tmp, minWordOccur))
                        iterator.remove();
                    removed = true;
                }

                if (!removed && minTfIdf > 0) {
                    if (tfIdfValue < this.minTfIdf)
                        iterator.remove();
                    removed = true;
                }

                if (!removed && maxTfIdf > 0) {
                    if (tfIdfValue > this.maxTfIdf)
                        iterator.remove();
                }
            }
        }
    }

}
