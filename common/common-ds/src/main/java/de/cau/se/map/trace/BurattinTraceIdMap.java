package de.cau.se.map.trace;

import java.util.HashMap;

public class BurattinTraceIdMap extends HashMap<Integer, TraceIdMapEntry> {

    public void insertOrUpdate(final Integer key, final String activity, final int newDelta) {
        if (containsKey(key)) {
            TraceIdMapEntry traceIdMapEntry = get(key);
            put(key,
                    new TraceIdMapEntry(activity,
                            traceIdMapEntry.getFrequency() + 1,
                            traceIdMapEntry.getDelta()));
        } else {
            put(key,
                    new TraceIdMapEntry(activity,
                            1,
                            newDelta));
        }
    }

    public void removeIrrelevantRelations(final int currentBucketId) {
        entrySet().removeIf(entry -> entry.getValue().getFrequency() + entry.getValue().getDelta() < currentBucketId);
    }
}
