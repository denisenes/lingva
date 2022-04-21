import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Tokenizer {

    public static ArrayList<String> tokenize(String text) {

            /*for (var t : tokens) {
                System.out.println("[" + t + "] " + Arrays.toString(t.getBytes(StandardCharsets.UTF_8)));
            }*/

        return Arrays.stream(text.
                        replace(",", "%,%").
                        replace(".", "%.%").
                        replace("!", "%!%").
                        replace(";", "%;%").
                        replace("?", "%?%").
                        replace(":", "%:%").
                        replace("=== ", "====").
                        replace(" ===", "%").
                        split("[\\[\\]%0123456789\"_ \n\r]")).
                filter(x -> x.length() > 0 && !x.equals("-")).
                collect(Collectors.toCollection(ArrayList::new));
    }

}
