import utils.*;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    static ArrayList<Model> parseModels(String path) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(path));
        ArrayList<Model> models = new ArrayList<>();
        int models_num = sc.nextInt();
        for (int i = 0; i < models_num; i++) {
            Model model = new Model();
            int units_num = sc.nextInt();

            boolean readNgram = false;


            for (int j = 0; j < units_num; j++) {
                String token = sc.next();

                if (token.equals("{")) {
                    readNgram = true;
                    model.createNewNgram();
                    continue;
                }
                if (token.equals("}")) {
                    readNgram = false; continue;
                }

                if (!readNgram) {
                    model.units.add(new SpecialWord(token));
                } else {
                    model.popNgram().words.add(new SpecialWord(token));
                }

            }
            models.add(model);
        }
        sc.close();
        return models;
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

        ArrayList<Model> models = parseModels("src/main/resources/models");

        /*
         * Split tokens into sentences
         */

        ArrayList<Sentence> sentences = new ArrayList<>();
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
            for (var unit : model.units) {
                System.out.print(unit.token + " ");
            }
            for (var ngram : model.ngrams) {
                System.out.print("{ ");
                ngram.words.forEach(x -> System.out.print(x.token + " "));
                System.out.println("} ");
            }
            System.out.println();

            List<Sentence> answer = findSentences(sentences, model, semantics);

            System.out.println("FOUND SENTENCES: " + answer.size());
            System.out.println("FREQ: " + ((double) answer.size() /  (double) sentences.size()));

            answer.stream().limit(5).forEach(x -> System.out.println(x.toString()));
            System.out.println();
        }

    }

    public static List<Sentence>
    findSentences(List<Sentence> sentences, Model model, List<SemanticUnit> semantics) {
        List<Sentence> rightSentences = new ArrayList<>();

        for (var sentence : sentences) {
            int counter = 0;
            for (var model_unit : model.units) {
                if (model_unit.isPartOfSpeech()) {

                    for (var word : sentence.lemmas) {
                        if (word.grammemes.size() > 0 && model_unit.token.equals(word.grammemes.get(0))) {
                            counter++;
                            break;
                        }
                    }

                } else {

                    SemanticUnit unit = null;
                    for (var s : semantics) {
                        if (s.unit_name.equals(model_unit.token)) {
                            unit = s;
                        }
                    }
                    assert unit != null;

                    for (var word : sentence.lemmas) {
                        if (unit.contains(word.word)) {
                            counter++;
                            break;
                        }
                    }
                }
            }

            for (var ngram : model.ngrams) {
                if (find_ngram(sentence, ngram)) {
                    counter++;
                }
            }

            if (counter == model.units.size() + model.ngrams.size()) {
                rightSentences.add(sentence);
            }
        }
        return rightSentences;
    }

    public static boolean find_ngram(Sentence s, Ngram ngram) {
        for (int i = 0; i < s.length()-ngram.words.size(); i++) {
            boolean found = true;
            for (int j = 0; j < ngram.words.size(); j++) {

                if (ngram.words.get(j).isPartOfSpeech()) {
                    if (s.get(i + j).grammemes.size() == 0 ||
                            !s.get(i + j).grammemes.get(0).equals(ngram.words.get(j).token)) {
                        found = false;
                    }
                } else {
                    if (!s.get(i + j).word.equals(ngram.words.get(j).token)) {
                        found = false;
                    }
                }
            }
            if (found) {
                return true;
            }
        }
        return false;
    }
}
