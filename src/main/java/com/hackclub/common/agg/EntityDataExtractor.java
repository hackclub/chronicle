package com.hackclub.common.agg;

/**
 * Interface to extract a DataType from a given EntityType
 * @param <EntityType> The type of entity you'd like to extract data from
 * @param <DataType> The type of the data you'd like to extract
 */
public interface EntityDataExtractor<EntityType, DataType> {
    DataType getValue(EntityType entity);
}
