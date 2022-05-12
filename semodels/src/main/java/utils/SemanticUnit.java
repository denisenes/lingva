package utils;

import java.util.ArrayList;

public class SemanticUnit {
    public String unit_name;
    public ArrayList<String> entries;

    public SemanticUnit(String unit_name) {
        this.unit_name = unit_name;
        entries = new ArrayList<>();
    }

    public boolean contains(String word) {
        for (var i : entries) {
            if (i.equals(word))
                return true;
        }
        return false;
    }
}
