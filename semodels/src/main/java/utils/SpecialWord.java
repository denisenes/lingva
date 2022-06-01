package utils;

import java.util.Arrays;

public class SpecialWord {
    String[] parts_of_speach = {"NOUN", "ADJF", "ADJS", "COMP", "VERB", "INFN",
            "PRTF", "PRTS", "GRND", "NUMR", "ADVB", "NPRO", "PRED", "PREP", "CONJ", "PRCL", "INTJ"};
    public String token;

    public SpecialWord(String token) {
        this.token = token;
    }

    public boolean isPartOfSpeech() {
        return Arrays.asList(parts_of_speach).contains(token);
    }
}
