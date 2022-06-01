package utils;

import morph.Morph;
import morph.Tokenizer;

import java.util.ArrayList;
import java.util.List;

public class Ngram {
    public List<String> words = new ArrayList<>();

    public Ngram(String text) {
        Morph morph = new Morph();
        ArrayList<String> tokens = Tokenizer.tokenize(text);
        ArrayList<Morph.AnnToken> annTokens = morph.lemmatize(tokens);
        for (var i : annTokens) {
            words.add(i.getLemma().word);
        }
    }

    public Ngram(List<String> l) {
        words = l;
    }

    public int size() {
        return words.size();
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("\"");
        for (var i : words)
            s.append(i).append(" ");
        s.setCharAt(s.length()-1, '\"');
        return s.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Ngram) {
            Ngram ngram = (Ngram) obj;

            if (ngram.words.size() != words.size())
                return false;

            for (int i = 0; i < words.size(); i++) {
                if (!ngram.words.get(i).equals(words.get(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
