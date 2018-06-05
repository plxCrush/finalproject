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
public class VectoralDataCreator implements DataCreator{

    private int upperLimit;
    private int upperLimitForOneTweet;
    private String outPath;
    HashMap<String, String> similarities;

    @Override
    public List<Tweet> create(List<Tweet> trainTweets) throws IOException {

        List<Tweet> createdTweets = new ArrayList<>();

        File out = new File(outPath);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out)));

        int counter = 0;

        for (Tweet t: trainTweets) {

            int perTweetCounter = 0;

            for(String word: t.getWords()) {

                if(similarities.containsKey(word)) {

                    String content, tag;
                    tag = t.getTag();
                    content = t.wordsToString();
                    content = content.replaceFirst(word, similarities.get(word));

                    Tweet created = new Tweet(tag, content, 0);
                    createdTweets.add(created);
                    counter++;
                    perTweetCounter++;

                    writer.write(t.getContent()+" || "+ created.getContent()+"\n");

                    if (perTweetCounter > 0 && perTweetCounter >= upperLimitForOneTweet)
                        break;
                }
            }

            if (upperLimit > 0 && counter >= upperLimit)
                break;
        }

        writer.flush();
        writer.close();

        return createdTweets;
    }

}
