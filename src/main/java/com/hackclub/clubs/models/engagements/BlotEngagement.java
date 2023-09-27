package com.hackclub.clubs.models.engagements;

import java.util.HashMap;

public class BlotEngagement {
    // Not publicly instantiable - use factory methods (fromCsv)
    private BlotEngagement() {
    }

    public static BlotEngagement fromCsv(String[] nextLine, HashMap<String, Integer> columnIndices) {
        BlotEngagement eng = new BlotEngagement();
        return eng;
    }
}
