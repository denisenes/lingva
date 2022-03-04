import java.util.*;

public class Morph {

    private class Record {
        Lemma lemma = null;
        String part = null;
        Integer freq = 0;
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

    public ArrayList<Record> processCorpora(ArrayList<String> tokens) {
        System.out.println("=== Result ===");
        int aboba = 0;
        for (var t : tokens) {
            ArrayList<Form> fs = forms.get(t);

            if (fs != null) {

                Set<Lemma> lls = new HashSet<>();
                for (var i : fs) {
                    lls.add(i.lemma);
                }

                if (lls.size() == 1) {
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
                } else {
                    //TODO omonims resolving
                    assert true;
                }
            }
        }
        System.out.println("// Общее количество рассмотренных слов: " + aboba);
        return new ArrayList<>(records.values());
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
