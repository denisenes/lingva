import utils.Lemma;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static boolean punctCheck(String str) {
        return str.equals(".") || str.equals(",") || str.equals(":") || str.equals(";") || str.equals("?");
    }

    public static void main(String[] args) throws IOException {

        // Integer freq_max = 100;

        ArrayList<Lemma> lemmas = new ArrayList<>();
        Morph morph = new Morph();

        // parse dictionary
        try (InputStream stream = new FileInputStream("src/main/resources/dict.opcorpora.xml")) {
            DictParser parser = new DictParser();

            parser.parse(stream, lemmas, morph);
        } catch (IOException | XMLStreamException e) {
            e.printStackTrace();
        }

        // get tokens from text
        String text = null;
        try {
            text = Files.readString(Paths.get("src/main/resources/bible.txt"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> tokens;
        assert text != null;
        tokens = Tokenizer.tokenize(text);

        // lemmatized tokens from corpora
        ArrayList<Morph.AnnToken> annTokens = morph.lemmatize(tokens);


        /*
         * Here we go...
         */

        // PARAMETERS
        TokensMap ngrams = new TokensMap();
        for (int n = 5; n < 8; n++) {

            // hashmap: Token
            int text_id = 0;

            // скользим по тексту и хэшируем строки
            for (int i = 1; i < annTokens.size() - n - 1; i++) { // a b c d === e f g
                List<String> ngram = new ArrayList<>();

                boolean wasDelim = false;
                for (int j = i; j < i + n; j++) {
                    if (annTokens.get(j).token.equals("====")) {
                        wasDelim = true;
                        break;
                    }
                    ngram.add(annTokens.get(j).getLemma());
                }

                if (wasDelim) {
                    text_id++;
                    i += 3;
                    continue;
                }

                ngrams.add(ngram, annTokens.get(i - 1).getLemma(),
                        annTokens.get(i + ngram.size() + 1).getLemma(), text_id);
            }
        }

        // delete freqs <= 1
        ngrams.map = ngrams.map.entrySet().stream().filter(x -> x.getValue().freq > 1).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        /*for (var i : ngrams.map.values()) {
            System.out.print("Freq: " + i.freq + " ");
            System.out.print("Text freq: " + i.text_ids.map.size() + " ");
            System.out.println(i.As);
            System.out.println();
        }*/

        /*
         * Let's check stability of ngrams
         */
        /**/
        double P = 0.9;
        System.out.println("=== Results ===");
        for (var i : ngrams.map.entrySet().stream().sorted(Comparator.comparing(x -> x.getValue().freq)).collect(Collectors.toList())) {
            Optional<Integer> maxAO = i.getValue().As.map.
                    values().stream().
                    max(Integer::compareTo);

            Optional<Integer> maxBO = i.getValue().Bs.map.
                    values().stream().
                    max(Integer::compareTo);

            if (maxAO.isPresent() && maxBO.isPresent()) {
                Integer maxA = maxAO.get();
                Integer maxB = maxBO.get();

                double f_xn = i.getValue().freq;
                double f_axn = maxA;
                double f_xnb = maxB;

                if (f_axn / f_xn <= P && f_xnb / f_xn <= P) {
                    System.out.print("<Ngram: ");
                    i.getKey().forEach(x -> System.out.print(x + " "));
                    System.out.println("| F: " + f_xn + " | TF: " +
                            i.getValue().text_ids.map.size() +
                            "| Устойч. Left:" + f_axn / f_xn + " Right: " + f_xnb / f_xn + ">");
                }
            }
        }
        System.out.print("=== Number of ngrams: " + ngrams.map.size() + " ===");
    }
}
