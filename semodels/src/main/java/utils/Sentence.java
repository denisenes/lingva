package utils;

import java.util.ArrayList;

public class Sentence {
    private final ArrayList<String> lemmas = new ArrayList<>();

    public void add(String lemma) {
        lemmas.add(lemma);
    }

    public String get(int i) {
        return lemmas.get(i);
    }

    public int length() {
        return lemmas.size();
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (var i : lemmas)
            s.append(i).append(" ");
        return s.toString();
    }
}
