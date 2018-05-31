package utils.read;

import lombok.AllArgsConstructor;
import lombok.Data;
import model.Tweet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

@Data
@AllArgsConstructor
public class WordSimilarReader {

    private String path;

    public HashMap<String, String> getWordSimilarities() {

        HashMap<String, String> similarities = new HashMap<>();

        similarities.put("key", "value");

        try {
            File file = new File(path);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(" ");
                similarities.put(parts[0], parts[1]);
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return similarities;

    }
}
