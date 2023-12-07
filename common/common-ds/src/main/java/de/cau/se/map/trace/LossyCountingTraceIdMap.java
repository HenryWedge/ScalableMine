package de.cau.se.map.trace;

import de.cau.se.map.result.FrequencyDeltaPair;

import java.util.HashMap;

public class LossyCountingTraceIdMap extends HashMap<Integer, LossyCountingTraceIdMap.Entry> {

    public void insertOrUpdate(final Integer key, final String activity, final int newDelta) {
        if (containsKey(key)) {
            Entry entry = get(key);
            put(key,
                    new Entry(activity,
                            entry.getFrequency() + 1,
                            entry.getDelta()));
        } else {
            put(key,
                    new Entry(activity,
                            1,
                            newDelta));
        }
    }

    public void removeIrrelevantRelations(final int currentBucketId) {
        entrySet().removeIf(entry -> entry.getValue().getFrequency() + entry.getValue().getDelta() < currentBucketId);
    }

    /**
     * This class stores the last occurring event of an trace beside frequency and delta
     */
    public class Entry extends FrequencyDeltaPair {

        final String lastEvent;

        public Entry(final String lastEvent, final int frequency, final int delta) {
            super(frequency, delta);
            this.lastEvent = lastEvent;
        }

        public String getLastEvent() {
            return lastEvent;
        }
    }
}
