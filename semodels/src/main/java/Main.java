import utils.Lemma;
import utils.SemanticUnit;
import utils.Sentence;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

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

        Scanner sc = new Scanner(new File("src/main/resources/semantics"));
        ArrayList<SemanticUnit> semantics = new ArrayList<>();
        int n = sc.nextInt();
        for (int i = 0; i < n; i++) {
            SemanticUnit unit = new SemanticUnit(sc.next());
            int lemm_n = sc.nextInt();
            for (int j = 0; j < lemm_n; j++)
                unit.entries.add(sc.next());
            semantics.add(unit);
        }

        /*for (var i : lemmas)
            System.out.println(i.word);*/

        sc = new Scanner(new File("src/main/resources/models"));
        ArrayList<ArrayList<String>> models = new ArrayList<>();
        int models_num = sc.nextInt();
        for (int i = 0; i < models_num; i++) {
            ArrayList<String> model = new ArrayList<>();
            int units_num = sc.nextInt();
            for (int j = 0; j < units_num; j++) {
                model.add(sc.next());
            }
            models.add(model);
        }
        sc.close();

        /*
         * Split tokens into sentences
         */

        ArrayList<Sentence> sentences = new ArrayList<Sentence>();
        Sentence new_sentence = new Sentence();
        for (var token : annTokens) {
            if (token.token.equals(".") || token.token.equals("!") || token.token.equals("?")) {
                sentences.add(new_sentence);
                new_sentence = new Sentence();
                continue;
            }
            new_sentence.add(token.getLemma());
        }

        /*
         * Then let's find sentences for every model
         */

        System.out.println("=== Semantic models ===");
        for (var model : models) {

            System.out.print("MODEL: ");
            for (var unit : model) {
                System.out.print(unit + " ");
            }
            System.out.println();

            List<Sentence> answer = findSentences(sentences, model, semantics);

            System.out.println("FOUND SENTENCES: " + answer.size());
            System.out.println("FREQ: " + ( (double) answer.size() /  (double) sentences.size()));

            answer.stream().limit(5).forEach(x -> System.out.println(x.toString()));
            System.out.println();
        }

    }

    public static List<Sentence>
    findSentences(List<Sentence> sentences, List<String> model, List<SemanticUnit> semantics) {
        List<Sentence> rightSentences = new ArrayList<>();

        for (var sentence : sentences) {
            Map<String, Boolean> result = new HashMap<>();
            for (int i = 0; i < sentence.length(); i++) {
                String word = sentence.get(i);

                for (var unit : semantics) {
                    if (model.contains(unit.unit_name) && unit.contains(word)) {
                        result.put(unit.unit_name, true);
                    }
                }

            }

            if (result.size() == model.size()) {
                rightSentences.add(sentence);
            }
        }
        return rightSentences;
    }
}
