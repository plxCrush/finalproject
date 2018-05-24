package model;

import com.graphbuilder.struc.Bag;
import lombok.Data;

import java.util.ArrayList;

@Data
public class Tweet {

    private String tag;
    private String content;
    private ArrayList<String> words;
    public byte[] bow;

    public Tweet (String tag, String content) {

        this.tag = tag;
        this.content = content;

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

    public void generateBow(Bag bag)
    {
        this.bow = new byte[bag.size()];

        for (String word : words) {
            if (bag.contains(word))
                this.bow[bag.indexOf(word)]++;
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
