package utils.read;

import com.graphbuilder.struc.Bag;
import model.Tweet;

import java.io.*;
import java.util.List;

public class ArffWriter {

    public void write(Bag bag, List<Tweet> tweets, String filePath, boolean writeTags) throws IOException {

        File file = new File(filePath);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));

        writer.write("@RELATION sentiment\n");

        for (int i = 0; i < bag.size(); i++)
        {
            writer.write("@ATTRIBUTE "+ "att"+i +" NUMERIC\n");
        }

        writer.write("@ATTRIBUTE class {pozitif,negatif,notr}\n@DATA\n");

        for (Tweet tweet: tweets) {

            writer.write(tweet.bow[0]+",");
            for (int j = 1; j < tweet.bow.length; j++)
            {
                writer.write(tweet.bow[j]+ ",");
            }

            if (writeTags)
                writer.write(tweet.getTag()+"\n");
            else
                writer.write("?\n");
        }

        writer.flush();
        writer.close();

    }

}
