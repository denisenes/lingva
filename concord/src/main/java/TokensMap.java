import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokensMap {

    class Info {
        List<String> ngram;
        Integer freq;

        Info(List<String> ngram) {
            this.ngram = ngram;
            this.freq = 1;
        }
    }

    public Map<List<String>, Info> map = new HashMap<>();

    public void add(List<String> tokens) {
        if (map.containsKey(tokens)) {
            map.get(tokens).freq += 1;
        } else {
            Info info = new Info(tokens);
            map.put(tokens, info);
        }
    }

}
