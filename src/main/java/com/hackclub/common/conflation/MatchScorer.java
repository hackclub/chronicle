package com.hackclub.common.conflation;

/**
 * An interface for defining a matching scorer which is responsible for relating two entities together via a scoring
 * metric.
 * @param <FromType>
 * @param <ToType>
 */
public interface MatchScorer<FromType, ToType> {
    double score(FromType from, ToType to);
    double getThreshold();
}
