package com.hackclub.common.agg;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Defines an entity processor, which consists of an aggregator (for bucketing/counting data points) and an extractor
 * (for extracting/transforming entity data into an aggregatable form)
 * @param <EntityType>
 * @param <AggregationType>
 */
public class EntityProcessor<EntityType, AggregationType> {
    private Aggregator<EntityType, AggregationType> aggregator;
    private EntityDataExtractor<EntityType, AggregationType> extractor;

    public EntityProcessor(Aggregator<EntityType, AggregationType> aggregator, EntityDataExtractor<EntityType, AggregationType> extractor) {
        this.aggregator = aggregator;
        this.extractor = extractor;
    }

    /**
     * Map over all entities, extracting data from each and aggregating it.  Note that this is NOT a terminal stream
     * operation.
     * @param entities The entities you'd like to process
     * @return The input Stream
     */
    public Stream<EntityType> process(Stream<EntityType> entities) {
        return entities.peek(entity -> aggregator.aggregate(entity, extractor.getValue(entity)));
    }

    public Map<String, AggregationType> getResults() {
        return aggregator.getResults();
    }
}
