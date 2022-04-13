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

        Record(Integer freq, SpecialSet As, SpecialSet Bs) {
            this.freq = freq;
            this.As = As;
            this.Bs = Bs;
        }
    }
    public Map<List<String>, Record> map = new HashMap<>();

    public void add(List<String> tokens, String a, String b) {
        if (map.containsKey(tokens)) {
            Integer freq = map.get(tokens).freq;
            freq++;

            SpecialSet as = map.get(tokens).As;
            as.add(a);

            SpecialSet bs = map.get(tokens).Bs;
            bs.add(b);

            map.put(tokens, new Record(freq, as, bs));
        } else {
            SpecialSet as = new SpecialSet();
            as.add(a);

            SpecialSet bs = new SpecialSet();
            bs.add(b);

            map.put(tokens, new Record(1, as, bs));
        }
    }

}
