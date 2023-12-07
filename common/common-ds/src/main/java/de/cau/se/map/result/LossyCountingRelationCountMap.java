package de.cau.se.map.result;

import java.util.HashMap;
import java.util.Optional;

public class LossyCountingRelationCountMap<K> extends HashMap<K, FrequencyDeltaPair> implements RelationCountMap<K, FrequencyDeltaPair> {

    public void removeIrrelevant(final Integer currentBucketId) {
        entrySet().removeIf(entry -> entry.getValue().getFrequency() + entry.getValue().getDelta() < currentBucketId);
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
}
