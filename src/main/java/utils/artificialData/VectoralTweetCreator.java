package utils.artificialData;

import model.Tweet;

import java.util.List;

public class VectoralTweetCreator implements DataCreator {

    List<Tweet> tweets;

    @Override
    public DataCreator withTweets(List<Tweet> tweets) {

        this.tweets = tweets;
        return this;
    }

    @Override
    public List<Tweet> create() {

        System.out.println("Creating VECTORAL test,\n"+ tweets.get(0).getContent());

        return tweets;
    }
}
