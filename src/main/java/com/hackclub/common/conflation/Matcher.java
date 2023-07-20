package com.hackclub.common.conflation;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * A helper class that defines an interface suitable for matching two datasets together.
 * @param <FromType>
 * @param <ToType>
 */
public class Matcher <FromType, ToType> {
    private Set<MatchResult<FromType, ToType>> results;

    /**
     * Constructor.  Takes in the input datasets, and a scorer that will be used to associate them
     * @param fromSet The input 'from' set
     * @param toSet The input 'to' set
     * @param scorer A scorer that will rank/relate items from each set
     */
    public Matcher(Set<FromType> fromSet, Set<ToType> toSet, MatchScorer<FromType, ToType> scorer) {
        match(fromSet, toSet, scorer);
    }

    private void match(Set<FromType> fromSet, Set<ToType> toSet, MatchScorer<FromType, ToType> scorer) {
        results = fromSet.parallelStream()
                .map(fromItem -> toSet.stream()
                    .map(toItem -> new MatchResult<>(fromItem, toItem, scorer.score(fromItem, toItem)))
                        .filter(match -> match.getScore() >= scorer.getThreshold())
                        .sorted(this::compareResults)
                )
                .flatMap(sortedResults -> sortedResults.findFirst().stream())
                .collect(Collectors.toSet());
    }

    private int compareResults(MatchResult<FromType, ToType> m1, MatchResult<FromType, ToType> m2) {
        return Double.compare(m1.getScore(), m2.getScore());
    }

    /**
     * Retrieves results from matching
     * @return A Set of MatchResult objects
     */
    public Set<MatchResult<FromType, ToType>> getResults() {
        return results;
    }
}
