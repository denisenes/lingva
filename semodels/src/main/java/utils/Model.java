package utils;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Model {

    public ArrayList<SpecialWord> units = new ArrayList<>();

    public boolean contains(String token) {
        return units.stream().map(x -> x.token).
                collect(Collectors.toList()).contains(token);
    }

    public ArrayList<Ngram> ngrams = new ArrayList<>();

    public void createNewNgram() {
        ngrams.add(new Ngram());
    }

    public Ngram popNgram() {
        return ngrams.get(ngrams.size()-1);
    }
}
