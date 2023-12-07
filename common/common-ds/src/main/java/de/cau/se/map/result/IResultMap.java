package de.cau.se.map.result;

import de.cau.se.datastructure.DirectlyFollowsRelation;

import java.util.Map;
import java.util.Set;

public interface IResultMap<K,V> extends Map<K,V> {

    void removeIrrelevant(final Integer currentBucketId);

    Set<K> getIrrelevant(final Integer currentBucketId);

    void insertOrUpdate(final K key, final Integer newDelta);

    Integer getCountOf(final K key);

    Set<K> getRelationsWithCountGreaterThan(int threshold);
}
