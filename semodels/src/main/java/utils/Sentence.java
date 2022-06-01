package utils;

import java.util.ArrayList;

public class Sentence {
    public final ArrayList<Lemma> lemmas = new ArrayList<>();

    public void add(Lemma lemma) {
        lemmas.add(lemma);
    }

    public Lemma get(int i) {
        return lemmas.get(i);
    }

    public int length() {
        return lemmas.size();
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (var i : lemmas)
            s.append(i.word).append(" ");
        return s.toString();
    }
}
