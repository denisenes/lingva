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
        return str.equals(".") || str.equals(",") || str.equals("===") || str.equals(":") || str.equals(";") || str.equals("?");
    }

    public static void main(String[] args) throws IOException {

        String phrase = "поднял руку";
        Integer neighbours_size = 4;
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

        ArrayList<Morph.AnnToken> annTokens = morph.lemmatize(tokens);

        // find occurrences of the phrase in tokens
        ArrayList<Morph.AnnToken> window = morph.lemmatize(Tokenizer.tokenize(phrase));
        ArrayList<Integer> occurs = new ArrayList<>();
        for (int window_shift = 0; window_shift < annTokens.size() - window.size(); window_shift++) {
            boolean found = true;
            for (int i = 0; i < window.size(); i++) {
                Set<String> intersection = new HashSet<>(annTokens.get(i+window_shift).lemmas);
                intersection.retainAll(window.get(i).lemmas);
                if (intersection.size() == 0) {
                    found = false;
                    break;
                }
            }
            if (found) {
                occurs.add(window_shift);
            }
        }

        System.out.println("All occurrences found");
        System.out.println(occurs.size());

        TokensMap lefts = new TokensMap();
        for (var i : occurs) {
            for (int left_shift = 0; left_shift < neighbours_size; left_shift++) {
                List<String> context = new ArrayList<>();
                boolean wrongPunct = false;
                // left shtuki
                for (int sh = left_shift; sh > 0; sh--) {
                    if (!wrongPunct) {
                        wrongPunct = punctCheck(annTokens.get(i - sh).token);
                    }
                    context.add(annTokens.get(i - sh).lemmas.stream().limit(1).collect(Collectors.joining()));
                }
                // middle shtuki
                for (int sh = 0; sh < window.size(); sh++) {
                    context.add(annTokens.get(i + sh).lemmas.stream().limit(1).collect(Collectors.joining()));
                }
                if (wrongPunct) {
                    break;
                }
                lefts.add(context);
            }
        }

        TokensMap rights = new TokensMap();
        for (var i : occurs) {
            for (int right_shift = 0; right_shift < neighbours_size; right_shift++) {
                List<String> context = new ArrayList<>();
                boolean wrongPunct = false;
                // middle shtuki
                for (int sh = 0; sh < window.size(); sh++) {
                    context.add(annTokens.get(i + sh).lemmas.stream().limit(1).collect(Collectors.joining()));
                }
                // right shtuki
                for (int sh = 0; sh < right_shift; sh++) {
                    if (!wrongPunct) {
                        wrongPunct = punctCheck(annTokens.get(i + window.size() + sh).token);
                    }
                    context.add(annTokens.get(i + window.size() + sh).lemmas.stream().limit(1).collect(Collectors.joining()));
                }
                if (wrongPunct) {
                    break;
                }
                rights.add(context);
            }
        }

        System.out.println("Left contexts: ");
        List<TokensMap.Info> resultsL = new ArrayList<>(lefts.map.values());
        resultsL.sort(Comparator.comparing(x -> x.freq));
        for (var i : resultsL) {
            System.out.print("<");
            i.ngram.forEach(x -> System.out.print(x + " "));
            System.out.println("> " + i.freq);
        }

        System.out.println("Right contexts: ");
        List<TokensMap.Info> resultsR = new ArrayList<>(rights.map.values());
        resultsR.sort(Comparator.comparing(x -> x.freq));
        for (var i : resultsR) {
            System.out.print("<");
            i.ngram.forEach(x -> System.out.print(x + " "));
            System.out.println("> " + i.freq);
        }


        // process text
        //morph.processTokens(tokens);
        //String result = morph.printRecords();

        // write result to output file
        /*File outputFile = new File("src/main/resources/output.txt");
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(result.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
