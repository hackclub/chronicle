package com.hackclub.common.agg;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Base class that defines an aggregation operation.  Given a list of objects of type EntityType and a bucketing
 * function, returns a Map<String, AggregationType>.  Often AggregationType will be a numeric type used for occurrence
 * counting.
 * @param <EntityType> The type of the object you want to aggregate
 * @param <AggregationType> The type you'll used for counting aggregations - often a basic numeric type like Integer/Double
 */
public abstract class Aggregator<EntityType, AggregationType> {
    protected ConcurrentHashMap<String,AggregationType> results = new ConcurrentHashMap<>();

    public void aggregate(EntityType entity, AggregationType value) {
        String key = bucket(entity);

        // Ignore unknown keys
        if (key == null) return;
        if (!results.containsKey(key)) {
            results.put(key, getDefaultValue());
        }
        results.put(key, add(results.get(key), value));
    }

    public abstract AggregationType add(AggregationType v1, AggregationType v2);

    public abstract String bucket(EntityType entity);

    public abstract AggregationType getDefaultValue();

    public Map<String, AggregationType> getResults() {
        return results;
    }
}
