package utils.read;

import model.Tweet;

import java.util.List;

public class TweetGroupInfo {

    public String sentimentDistrubiton(List<Tweet> tweets) {

        int poCounter = 0;
        int neCounter = 0;
        int po, ne, no;

        for (Tweet t : tweets) {
            if (t.getTag().equals("pozitif"))
                poCounter++;
            if (t.getTag().equals("negatif"))
                neCounter++;
        }

        po = (poCounter*100)/tweets.size();
        ne = (neCounter*100)/tweets.size();
        no = 100 - (po + ne);

        if (no == 100)
            return "\nUntagged test data";

        return ("\nPositive: %"+ po + ", Negative: %"+ ne + ", Neutral: %"+ no);
    }
}
