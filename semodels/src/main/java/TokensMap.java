import java.util.*;

public class TokensMap {

    public class SpecialSet {

        public HashMap<String, Integer> map = new HashMap<>();

        public void add(String a) {
            if (map.containsKey(a)) {
                Integer f = map.get(a);
                f++;
                map.put(a, f);
            } else {
                map.put(a, 1);
            }
        }

        public int getSize() {
            return map.size();
        }

        @Override
        public String toString() {
            StringBuilder out = new StringBuilder("AList {");
            for (var i : map.entrySet()) {
                out.append(i.getKey()).append(" : ");
                out.append(i.getValue()).append(", ");
            }
            return out + "}";
        }
    }

    class Record {
        Integer freq;
        SpecialSet As;
        SpecialSet Bs;
        SpecialSet text_ids;

        Record(Integer freq, SpecialSet As, SpecialSet Bs, SpecialSet text_ids) {
            this.freq = freq;
            this.As = As;
            this.Bs = Bs;
            this.text_ids = text_ids;
        }
    }
    public Map<List<String>, Record> map = new HashMap<>();

    public void add(List<String> tokens, String a, String b, int text_id) {
        if (map.containsKey(tokens)) {
            Integer freq = map.get(tokens).freq;
            freq++;

            SpecialSet as = map.get(tokens).As;
            as.add(a);

            SpecialSet bs = map.get(tokens).Bs;
            bs.add(b);

            SpecialSet text_ids = map.get(tokens).text_ids;
            text_ids.add(String.valueOf(text_id));

            map.put(tokens, new Record(freq, as, bs, text_ids));
        } else {
            SpecialSet as = new SpecialSet();
            as.add(a);

            SpecialSet bs = new SpecialSet();
            bs.add(b);

            SpecialSet text_ids = new SpecialSet();
            text_ids.add(String.valueOf(text_ids));

            map.put(tokens, new Record(1, as, bs, text_ids));
        }
    }

}
