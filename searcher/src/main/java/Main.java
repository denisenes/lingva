import morph.DictParser;
import morph.Morph;
import morph.Tokenizer;
import utils.*;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

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

        /*
         * Read thesaurus
         */

        ArrayList<Sinset> sinsets = ThesaurusReader.read();

        ArrayList<Text> texts = new ArrayList<>();

        /*
         *  Query processing
         */

        List<String> queries = Files.readAllLines(
                new File("src/main/resources/queries.txt").toPath(),
                        Charset.defaultCharset());

        int queries_n = queries.size();

        for (var i : queries) {
            var ts = Tokenizer.tokenize(i);
            List<String> annTs = morph.lemmatize(ts).
                    stream().map(x -> x.getLemma().word).
                    collect(Collectors.toList());

            Text text1 = new Text();
            text1.tokens = annTs;
            texts.add(text1);
        }

        /*
         * Make texts
         */

        Text new_text = new Text();
        for (var token : annTokens) {
            if (token.token.equals("====") && (new_text.tokens.size() > 0)) {
                texts.add(new_text);
                new_text = new Text();
                continue;
            }
            if (!token.token.equals("===="))
                new_text.tokens.add(token.getLemma().word);
        }
        texts.add(new_text);

        /*
         *  Indexation phase
         */

        System.out.println("Текстов: " + texts.size());
        for (var t : texts) {
            for (var i : sinsets) {
                t.entries.put(i, new Text.Context(0, 0));
            }
            for (var i : sinsets)
                t.computeFreq(i);
            t.computeTf();
        }

        for (var t : texts) {
            for (var i : sinsets)
                t.computeIdf(texts, i);
            //System.out.println(t);
        }

        /*
         *   Search
         */

        for (int i = 0; i < queries_n; i++) {
            System.out.println();
            System.out.println("=============================");
            System.out.println("Запрос: " + texts.get(i));

            List<Result> results = new ArrayList<>();

            for (int j = queries_n; j < texts.size(); j++) {
                Result res = new Result();
                res.cosResult = cosineSimilarity(texts.get(i), texts.get(j));
                res.textId = j;
                results.add(res);
            }

            results.sort(Comparator.comparingDouble(x -> x.cosResult));

            results.stream().filter(x -> x.cosResult > 0.80)
                    .limit(7)
                    .forEach(x -> {
                        System.out.println("----------------------");
                        System.out.println("Релевантность: " + x.cosResult);
                        System.out.println(texts.get(x.textId));
                    });
        }

    }

    static class Result {
        double cosResult;
        int textId;
    }

    static double cosineSimilarity(Text t1, Text t2) {
        Set<Sinset> keys = t1.entries.keySet();

        // compute scalar product
        double product = 0f;
        for (var k : keys) {
            product += (t1.entries.get(k).tf * t1.entries.get(k).idf) *
                    (t2.entries.get(k).tf * t2.entries.get(k).idf);
        }

        double len1 = 0f;
        for (var k : keys) {
            len1 += Math.pow(t1.entries.get(k).tf * t1.entries.get(k).idf, 2);
        }
        len1 = Math.sqrt(len1);

        double len2 = 0f;
        for (var k : keys) {
            len2 += Math.pow(t2.entries.get(k).tf * t2.entries.get(k).idf, 2);
        }
        len2 = Math.sqrt(len2);

        return product / (len1 * len2);
    }
}

