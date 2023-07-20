package com.hackclub.common.conflation;

/**
 * Immutable class that represents how closely related two entities are
 * @param <FromType>
 * @param <ToType>
 */
public class MatchResult <FromType, ToType> {
    private FromType from;
    private ToType to;
    private double score;

    public MatchResult(FromType from, ToType to, double score) {
        this.from = from;
        this.to = to;
        this.score = score;
    }

    public FromType getFrom() {
        return from;
    }

    public ToType getTo() {
        return to;
    }

    public double getScore() {
        return score;
    }
}
