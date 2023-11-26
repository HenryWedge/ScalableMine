package de.cau.se.datastructure;

import java.util.HashMap;

public class ActivityMap extends HashMap<String, ActivityMap.ActivityMapEntry> {

    public void accept(final String activity, final int bucketId) {
        if (containsKey(activity)) {
            get(activity).incrementFrequency().updateBucketId(bucketId);
        } else {
            put(activity, new ActivityMapEntry(1, bucketId - 1));
        }
    }

    public class ActivityMapEntry {
        int frequency;
        int delta;

        public ActivityMapEntry(final int frequency, final int incomingBucketId) {
            this.frequency = frequency;
            this.delta = incomingBucketId;
        }

        public ActivityMapEntry incrementFrequency () {
            frequency++;
            return this;
        }

        public ActivityMapEntry updateBucketId(int incomingBucketId) {
            this.delta = incomingBucketId;
            return this;
        }

        public boolean isRelevant(final Integer bucketId) {
            return bucketId < frequency + delta;
        }
    }
}
