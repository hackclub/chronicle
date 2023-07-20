package com.hackclub.common.agg;

import java.util.function.Function;

/**
 * A concrete Double-type Aggregator that takes a custom bucketing function
 * @param <EntityType> The type of entity we'd like to aggregate
 */
public class DoubleAggregator<EntityType> extends Aggregator<EntityType, Double> {
    private Function<EntityType, String> bucketer;

    /**
     * Creates a new DoubleAggregator
     * @param bucketer A user-defined bucketing function.
     */
    public DoubleAggregator(Function<EntityType, String> bucketer) {
        this.bucketer = bucketer;
    }

    public Double add(Double v1, Double v2) {
        return v1 + v2;
    }

    @Override
    public String bucket(EntityType entity) {
        return bucketer.apply(entity);
    }

    public Double getDefaultValue() {
        return 0.0;
    }
}
