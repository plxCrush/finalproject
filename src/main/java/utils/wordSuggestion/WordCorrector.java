package utils.wordSuggestion;

import zemberek.morphology.TurkishMorphology;
import zemberek.normalization.TurkishSpellChecker;

import java.io.IOException;
import java.util.List;

public class WordCorrector {

    TurkishMorphology morphology;
    TurkishSpellChecker spellChecker;

    public WordCorrector() throws IOException {

        this.morphology = TurkishMorphology.createWithDefaults();
        this.spellChecker = new TurkishSpellChecker(morphology);
    }

    public String testCase(String x) throws IOException {

        TurkishMorphology morphology = TurkishMorphology.createWithDefaults();
        TurkishSpellChecker spellChecker = new TurkishSpellChecker(morphology);

        System.out.println("Check if written correctly.");
        String[] words = {"Ankara'ya", "Ankar'aya", "yapbileceksen", "yapabileceğinizden"};
        for (String word : words) {
            System.out.println(word + " -> " + spellChecker.check(word));
        }
        System.out.println();
        System.out.println("Give suggestions.");
        String[] toSuggest = {"Kraamanda", "okumuştk", "yapbileceksen", "oukyamıyorum"};
        for (String s : toSuggest) {
            System.out.println(s + " -> " + spellChecker.suggestForWord(s));
        }

        return x;
    }

    public List<String> suggestWords(String x) {
        return spellChecker.suggestForWord(x);
    }

    public boolean checkWord(String x) {
        return spellChecker.check(x);
    }

}
