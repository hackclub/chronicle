package com.hackclub.clubs.models.engagements;

import java.util.HashMap;

public class OnboardEngagement {
    // Not publicly instantiable - use factory methods (fromCsv)
    private OnboardEngagement() {
    }

    public static OnboardEngagement fromCsv(String[] nextLine, HashMap<String, Integer> columnIndices) {
        OnboardEngagement eng = new OnboardEngagement();
        return eng;
    }
}
