package de.cau.se.map.result;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MicroBatchRelationCountMap<K> extends HashMap<K, Integer> implements RelationCountMap<K, Integer> {

    @Override
    public void insertOrUpdate(final K key, final Integer count) {
        if (containsKey(key)) {
            put(key, get(key) + count);
        } else {
            put(key, count);
        }
    }

    @Override
    public Integer getCountOf(final K key) {
        return Optional.ofNullable(get(key)).orElse(0);
    }

    public Set<K> getRelevantRelations(final int threshold) {
        return keySet().stream().filter(key -> getCountOf(key) > threshold).collect(Collectors.toSet());
    }

    public Set<K> getIrrelevantRelations(final int threshold) {
        return keySet().stream().filter(key -> get(key) <= threshold).collect(Collectors.toSet());
    }

    @Override
    public int size() {
        return values().stream().reduce(0, Integer::sum);
    }
}
