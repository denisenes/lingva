import utils.Form;
import utils.Lemma;

import java.util.*;

public class Morph {

    public Morph() {
    }

    private class Record {
        Lemma lemma = null;
        String part = null;
        Integer freq = 0;
    }

    public class AnnToken {
        String token = null;
        Set<String> lemmas = null;

        AnnToken(String token, Set<String> lemmas) {
            this.token = token;
            this.lemmas = lemmas;
        }

        String getLemma() {
            return lemmas.iterator().next();
        }
    }

    String[] parts_of_speach = {"NOUN", "ADJF", "ADJS", "COMP", "VERB", "INFN",
            "PRTF", "PRTS", "GRND", "NUMR", "ADVB", "NPRO", "PRED", "PREP", "CONJ", "PRCL", "INTJ"};

    private final HashMap<String, ArrayList<Form>> forms = new HashMap<>();
    private final HashMap<Lemma, Record> records = new HashMap<>();

    public void put(String key, Form value) {
        if (forms.containsKey(key)) {
            forms.get(key).add(value);
        } else {
            ArrayList<Form> fs = new ArrayList<>();
            fs.add(value);
            forms.put(key, fs);
        }
    }

    public ArrayList<Form> get(String key) {
        return forms.get(key);
    }

    public ArrayList<Record> processTokens(ArrayList<String> tokens) {
        System.out.println("=== Result ===");
        int aboba = 0;
        int notFound = 0;

        double error = 0;
        double n = 0;


        for (var t : tokens) {
            ArrayList<Form> fs = forms.get(t);

            if (fs != null) {

                Set<Lemma> lls = new HashSet<>();
                for (var i : fs) {
                    lls.add(i.lemma);
                }

                if (lls.size() == 1) {
                    assert true;
                } else {
                    // compute error
                    double size = lls.size();
                    error += (size-1)/size;
                }
                Lemma l = ((Lemma) lls.toArray()[0]);
                aboba++;
                if (records.containsKey(l)) {
                    Record record = records.get(l);
                    record.freq += 1;
                } else {
                    Record record = new Record();
                    record.lemma = l;
                    record.part = l.grammemes.get(0); // part of speech is always in 0 index of grammemes
                    record.freq = 1;
                    records.put(l, record);
                }
                n++;
            }
            else {
                notFound++;
                aboba++;

            }
        }
        error = error / n;
        //System.out.println("// Нераспознано слов (%): " + (notFound * 100 / (n + notFound)));
        //System.out.println("// Средняя точность определения леммы слова: " + ((1 - error)*100));
        //System.out.println("// Общее количество рассмотренных слов: " + aboba);
        return new ArrayList<>(records.values());
    }

    public ArrayList<AnnToken> lemmatize(ArrayList<String> tokens) {
        ArrayList<AnnToken> annTokens = new ArrayList<>();

        for (var t : tokens) {
            ArrayList<Form> fs = forms.get(t);

            if (fs != null) {

                Set<String> lls = new HashSet<>();
                for (var i : fs) {
                    lls.add(i.lemma.word);
                }

                annTokens.add(new AnnToken(t, lls));

            } else {
                Set<String> lemmas = new HashSet<>();
                lemmas.add(t);
                annTokens.add(new AnnToken(t, lemmas));
            }
        }

        return annTokens;
    }

    public void printWordInfo(String key) {
        System.out.println("Форма: " + key);
        ArrayList<Form> fs = forms.get(key);
        for (var i : fs) {
            System.out.println("=================");
            System.out.println("Лемма : " + i.lemma.word);
            System.out.print("Морфологические признаки леммы: ");
            i.lemma.grammemes.forEach(x -> System.out.print(x + " "));
            System.out.println();
            System.out.print("Морфологические признаки формы: ");
            i.grammemes.forEach(x -> System.out.print(x + " "));
            System.out.println();
        }
    }

    public String printRecords() {
        StringBuilder sb = new StringBuilder();
        ArrayList<Record> records_arr = new ArrayList<>(records.values());
        records_arr.sort(Comparator.comparing(r -> r.freq));
        for (var i : records_arr) {
            System.out.println(i.lemma.word + " " + i.freq + " " + i.part);
            sb.append(i.lemma.word).append(" ").append(i.freq).append(" ").append(i.part).append("\n");
        }
        return sb.toString();
    }
}
