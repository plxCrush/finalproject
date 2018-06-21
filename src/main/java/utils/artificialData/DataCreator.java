package utils.artificialData;

import model.Tweet;

import java.io.IOException;
import java.util.List;

public interface DataCreator {

    List<Tweet> create(List<Tweet> trainTweets) throws IOException;
}
