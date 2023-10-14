package com.hackclub.common.conflation;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A helper class that defines an interface suitable for matching two datasets together.
 * @param <FromType>
 * @param <ToType>
 */
public class Matcher <FromType, ToType> {
    private Set<MatchResult<FromType, ToType>> results;
    private Set<FromType> unmatchedFrom = new HashSet<>();
    private Set<ToType> unmatchedTo = new HashSet<>();;
    private int fromSize;
    private int toSize;

    /**
     * Constructor.  Takes in the input datasets, and a scorer that will be used to associate them
     * @param fromSet The input 'from' set
     * @param toSet The input 'to' set
     * @param scorer A scorer that will rank/relate items from each set
     */
    public Matcher(String matcherName, Set<FromType> fromSet, Set<ToType> toSet, MatchScorer<FromType, ToType> scorer) {
        match(fromSet, toSet, scorer);
        calculateStatistics(matcherName, fromSet, toSet, results);
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

        unmatchedFrom.addAll(fromSet.stream().filter(f -> !resultSetContainsFrom(f, results)).collect(Collectors.toSet()));
        unmatchedTo.addAll(toSet.stream().filter(t -> !resultSetContainsTo(t, results)).collect(Collectors.toSet()));
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

    public void calculateStatistics(String matcherName, Set<FromType> fromSet, Set<ToType> toSet, Set<MatchResult<FromType, ToType>> resultSet) {
        double fromSize = fromSet.size();
        double toSize = toSet.size();
        double fromsContainedInResults = fromSet.size() - unmatchedFrom.size();
        double tosContainedInResults = toSet.size() - unmatchedTo.size();
        double totalResults = resultSet.size();

        System.out.printf("RUNNING MATCHER \"%s\"\r\n", matcherName);
        System.out.printf("\tFROM coverage: %.02f%%\r\n", (fromsContainedInResults / fromSize) * 100.0f);
        System.out.printf("\t  TO coverage: %.02f%%\r\n", (tosContainedInResults / toSize) * 100.0f);
    }
    private boolean resultSetContainsFrom(FromType f, Set<MatchResult<FromType, ToType>> resultSet) {
        return resultSet.stream().anyMatch(r -> r.getFrom() == f);
    }
    private boolean resultSetContainsTo(ToType t, Set<MatchResult<FromType, ToType>> resultSet) {
        return resultSet.stream().anyMatch(r -> r.getTo() == t);
    }
    public Set<FromType> getUnmatchedFrom() {
        return unmatchedFrom;
    }

    public Set<ToType> getUnmatchedTo() {
        return unmatchedTo;
    }
}
