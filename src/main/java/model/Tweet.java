package model;

import com.graphbuilder.struc.Bag;
import lombok.Data;
import utils.bag.TfIdfCalculator;

import java.util.ArrayList;
import java.util.List;

@Data
public class Tweet {

    private String tag;
    private String content;
    private List<String> words;
    public double[] bow;
    private int originalSeq;

    public Tweet (String tag, String content, int originalSeq) {

        this.tag = tag;
        this.content = content;
        this.originalSeq = originalSeq;

        this.words = new ArrayList<>();
        String tmp[] = this.content.split(" ");
        for (String word : tmp) {
            if (!containsLink(word)) {
                String x = clearContent(word);
                if (!x.equals("") && x.length()>1)
                    this.words.add(x);
            }
        }
    }

    public String wordsToString() {

        StringBuilder x = new StringBuilder();

        for(String word : this.words ) {
            x.append(word).append(" ");
        }

        return x.toString().trim();
    }

    public void resetWords() {

        this.words = new ArrayList<>();
        String tmp[] = this.content.split(" ");
        for (String word : tmp) {
            if (!containsLink(word)) {
                String x = clearContent(word);
                if (!x.equals("") && x.length()>1)
                    this.words.add(x);
            }
        }
    }

    private String clearContent(String x) {

        x = x.replaceAll("[é!…'^+%&./()=?_><#${}’1234567890*-/*-+:,~¨]","");
        x = x.toLowerCase();
        x = x.trim();
        return x;
    }

    private boolean containsLink(String word) {

        return word.contains("/") || word.contains("\\") || word.contains("@") || word.equals("");
    }

    public void changeWord(String word, String newWord) {

        this.getWords().set(getWords().indexOf(word), newWord);
    }

    public void generateBow(Bag bag, List<List<String>> allTweets, String representation) {

        this.bow = new double[bag.size()];

        TfIdfCalculator calculator = new TfIdfCalculator();

        for (String word : words) {

            if (bag.contains(word)) {

                if (representation.equals("tf")) {

                    bow[bag.indexOf(word)] = calculator.tf(this.getWords(), word);
                }
                else if (representation.equals("tfIdf")) {

                    bow[bag.indexOf(word)] = calculator.tfIdf(this.getWords(), allTweets, word);
                }
            }
        }

    }

    public void print() {

        StringBuilder words = new StringBuilder();
        for (String x : this.words)
            words.append(" - ").append(x);

        System.out.println(" ");
        System.out.println("CONTENT : "+ this.content);
        System.out.println("TAG     : "+ this.tag);
        System.out.println("WORDS   : "+ words);

    }

}
