package com.hackclub.common.agg;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class EntityProcessorTest {
    @Test
    public void basic() {
        Function<String, String> strLengthBucketer = str -> Integer.toString(str.length());
        Aggregator<String, Double> aggregator = new DoubleAggregator<>(strLengthBucketer);
        EntityDataExtractor<String, Double> extractor = entity -> null;
        EntityProcessor<String, Double> processor = new EntityProcessor<>(aggregator, extractor);
    }

}