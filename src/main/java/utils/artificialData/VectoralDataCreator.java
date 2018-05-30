package utils.artificialData;

import lombok.AllArgsConstructor;
import lombok.Data;
import model.Tweet;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor

public class VectoralDataCreator {

    private int upperLimit;

    public List<Tweet> create(List<Tweet> trainTweets) {

        List<Tweet> createdTweets = new ArrayList<>();

        //TODO: return created tweets and write them to file here

        return createdTweets;
    }

}
