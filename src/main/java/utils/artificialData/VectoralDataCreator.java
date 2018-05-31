package utils.artificialData;

import lombok.AllArgsConstructor;
import lombok.Data;
import model.Tweet;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
public class VectoralDataCreator {

    private int upperLimit;
    private String outPath;

    public List<Tweet> create(List<Tweet> trainTweets, HashMap<String, String> similarities) throws IOException {

        List<Tweet> createdTweets = new ArrayList<>();

        File out = new File(outPath);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out)));

        int counter = 0;

        for (Tweet t: trainTweets) {

            for(String word: t.getWords()) {

                if(similarities.containsKey(word)) {

                    String content, tag;
                    tag = t.getTag();
                    content = t.wordsToString();
                    content = content.replaceFirst(word, similarities.get(word));

                    Tweet created = new Tweet(tag, content);
                    createdTweets.add(created);
                    counter++;

                    writer.write(t.getContent()+" - "+ created.getContent()+"\n");

                    break;
                }
            }

            if (counter >= upperLimit)
                break;
        }

        writer.flush();
        writer.close();

        return createdTweets;
    }

}
