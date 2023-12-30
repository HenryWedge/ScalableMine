package de.cau.se.map.result;

import java.util.Map;


public interface RelationCountMap<K,V> extends Map<K,V> {

    /**
     * Inserts a value if its not present. Otherwise it is updated by the passed new value.
     * @param key
     * @param newValue
     */
    void insertOrUpdate(final K key, final Integer newValue);

    /**
     * Returns the number of occurrences of a activity (the key)
     * @param key key of the map
     * @return Number of occurrences per key
     */
    Integer getCountOf(final K key);
}
