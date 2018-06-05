package utils.artificialData;

import lombok.AllArgsConstructor;
import lombok.Data;
import model.Tweet;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class TranslationDataCreator implements DataCreator {

    List<Tweet> allTweets;
    int size;
    private String outPath;

    @Override
    public List<Tweet> create(List<Tweet> trainTweets) throws IOException {

        File out = new File(outPath);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out)));

        List<Tweet> createdTweets = new ArrayList<>();

        for (Tweet trainTweet: trainTweets) {

            Tweet translatedTweet = allTweets.get(trainTweet.getOriginalSeq() + size);

            if (!isSame(trainTweet, translatedTweet)) {

                createdTweets.add(translatedTweet);
                writer.write(trainTweet.getContent()+" || "+ translatedTweet.getContent()+"\n");
            }

        }

        writer.flush();
        writer.close();

        return createdTweets;
    }

    private boolean isSame(Tweet t1, Tweet t2) {

        return t1.getContent().equals(t2.getContent());
    }
}
