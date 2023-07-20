package com.hackclub.common.agg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

class DoubleAggregatorTest {
    @Test
    void basic() {
        // Buckets string values by their length
        Function<String, String> bucketer = str -> Integer.toString(str.length());
        DoubleAggregator<String> characterCounter = new DoubleAggregator<>(bucketer);
        characterCounter.aggregate("testing1", 1.0);
        characterCounter.aggregate("testing2", 1.0);
        Assertions.assertEquals(1, characterCounter.getResults().size());

        characterCounter.aggregate("test", 1.0);
        Assertions.assertEquals(2, characterCounter.getResults().size());

        Assertions.assertEquals(characterCounter.getResults().get("8"), 2);
    }
}