package de.cau.se.map.result;

import java.util.Map;

public interface RelationCountMap<K,V> extends Map<K,V> {
    void insertOrUpdate(final K key, final Integer newDelta);
    
    Integer getCountOf(final K key);
}
