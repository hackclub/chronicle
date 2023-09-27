package com.hackclub.clubs.models.engagements;

import java.util.HashMap;

public class SprigEngagement {
    // Not publicly instantiable - use factory methods (fromCsv)
    private SprigEngagement() {
    }

    public static SprigEngagement fromCsv(String[] nextLine, HashMap<String, Integer> columnIndices) {
        SprigEngagement eng = new SprigEngagement();
        return eng;
    }
}
