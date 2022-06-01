import com.google.gson.Gson;
import utils.Ngram;
import utils.Sinset;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class ThesaurusReader {

    static class Thesaurus {
        JSONSinset [] sinsets;
    }

    static class JSONSinset {
        String descriptor;
        String [] synonyms;
        String [] hyperonims;
        String [] hyponims;
    }

    private static void repairPointers(Thesaurus thes, ArrayList<Sinset> sinsets) {
        for (var sinset : sinsets) {

            JSONSinset current = Arrays.stream(thes.sinsets).
                    filter(x -> sinset.descriptor.equals(x.descriptor)).
                    findAny().orElse(null);

            assert current != null;

            for (var hyperonim : current.hyperonims) {
                Sinset higher = sinsets.stream().filter(x -> hyperonim.equals(x.descriptor)).
                        findAny().orElse(null);
                sinset.hyperonims.add(higher);
            }

            for (var hyponim : current.hyponims) {
                Sinset lower = sinsets.stream().filter(x -> hyponim.equals(x.descriptor)).
                        findAny().orElse(null);
                sinset.hyponims.add(lower);
            }
        }
    }

    static ArrayList<Sinset> read() throws IOException {
        System.out.println("Thesaurus parsing...");

        String text = Files.readString(Paths.get("src/main/resources/Thesaurus.json"));
        Thesaurus thesaurus;
        Gson g = new Gson();

        thesaurus = g.fromJson(text, Thesaurus.class);

        ArrayList<Sinset> sinsets = new ArrayList<>();
        for (var i : thesaurus.sinsets) {
            Sinset sinset = new Sinset(i.descriptor);
            for (var syn : i.synonyms) {
                sinset.synonyms.add(new Ngram(syn));
            }
            sinsets.add(sinset);
        }

        repairPointers(thesaurus, sinsets);

        return sinsets;
    }

}
