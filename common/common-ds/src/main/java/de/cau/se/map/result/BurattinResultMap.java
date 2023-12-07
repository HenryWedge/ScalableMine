package de.cau.se.map.result;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class BurattinResultMap<K> extends HashMap<K, FrequencyDeltaPair> implements IResultMap<K, FrequencyDeltaPair> {

    public void removeIrrelevant(final Integer currentBucketId) {
        entrySet().removeIf(entry -> entry.getValue().getFrequency() + entry.getValue().getDelta() < currentBucketId);
    }

    @Override
    public Set<K> getIrrelevant(Integer currentBucketId) {
        return keySet().stream().filter(key -> getCountOf(key) < currentBucketId).collect(Collectors.toSet());
    }

    public void insertOrUpdate(final K key, final Integer newDelta) {
        final FrequencyDeltaPair value = get(key);
        if (containsKey(key)) {
            put(key,
                    new FrequencyDeltaPair(value.getFrequency() + 1,
                            value.getDelta()));
        } else {
            put(key, new FrequencyDeltaPair(1, newDelta));
        }
    }

    @Override
    public Integer getCountOf(K key) {
        return Optional.ofNullable(get(key)).map(FrequencyDeltaPair::getFrequency).orElse(0);
    }

    @Override
    public Set<K> getRelationsWithCountGreaterThan(final int threshold) {
        return keySet().stream().filter(key -> getCountOf(key) > threshold).collect(Collectors.toSet());
    }
}
