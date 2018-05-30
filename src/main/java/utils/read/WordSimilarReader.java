package utils.read;

import lombok.AllArgsConstructor;
import lombok.Data;
import model.WordSimilar;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class WordSimilarReader {

    private String path;

    public List<WordSimilar> getWordSimilarities() {

        List<WordSimilar> wordSimilarities = new ArrayList<>();

        //TODO: read similarity file line by line and push to arraylist here

        return wordSimilarities;

    }
}
