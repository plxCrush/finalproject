package utils.artificialData;

import model.Tweet;

import java.util.List;

public interface DataCreator {

    DataCreator withTweets(List<Tweet> tweets);
    List<Tweet> create();

}
