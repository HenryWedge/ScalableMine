package de.cau.se;

import de.cau.se.datastructure.DirectlyFollows;
import de.cau.se.datastructure.Event;
import de.cau.se.model.ModelUpdater;
import org.apache.kafka.clients.consumer.Consumer;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class BurattinSink extends AbstractConsumer<Event> {

    int n = 1;

    private Map<String, FrequencyDeltaPair> activityMap = new HashMap<>();
    private Map<Integer, TraceIdMapEntry> traceIdMap = new HashMap<>();
    private Map<DirectlyFollows, FrequencyDeltaPair> directlyFollowsMap = new HashMap<>();

    //private ModelUpdater modelUpdater = new ModelUpdater();

    int bucketSize;

    public BurattinSink(Consumer<String, Event> consumer) {
        super(consumer);
    }

    @Override
    protected void receive(Event event) {
        final int currentBucketId = (int) Math.ceil((double) n / bucketSize);

        final String activity = event.getActivity();
        if (activityMap.containsKey(activity)) {
            FrequencyDeltaPair directlyFollowsMapEntry = directlyFollowsMap.get(activity);
            activityMap.put(activity,
                    new FrequencyDeltaPair(
                            directlyFollowsMapEntry.frequency + 1,
                            directlyFollowsMapEntry.delta));
        } else {
            activityMap.put(activity, new FrequencyDeltaPair(1, currentBucketId - 1));
        }

        if (!traceIdMap.containsKey(event.getTraceId())) {
            traceIdMap.put(event.getTraceId(),
                    new TraceIdMapEntry(event.getActivity(),
                            1,
                            currentBucketId - 1));
        } else {
            TraceIdMapEntry traceIdMapEntry = traceIdMap.get(event.getTraceId());
            traceIdMap.put(event.getTraceId(),
                    new TraceIdMapEntry(event.getActivity(),
                            traceIdMapEntry.frequency + 1,
                            traceIdMapEntry.delta));

            final DirectlyFollows newDirectlyFollows = new DirectlyFollows(traceIdMapEntry.lastEvent, event.getActivity());
            if (directlyFollowsMap.containsKey(newDirectlyFollows)) {
                FrequencyDeltaPair directlyFollowsMapEntry = directlyFollowsMap.get(newDirectlyFollows);
                directlyFollowsMap.put(newDirectlyFollows,
                        new FrequencyDeltaPair(
                                directlyFollowsMapEntry.frequency + 1,
                                directlyFollowsMapEntry.delta));
            } else {
                directlyFollowsMap.put(newDirectlyFollows, new FrequencyDeltaPair(1, currentBucketId - 1));
            }
        }

        activityMap = activityMap.entrySet().stream()
                .filter(entry -> entry.getValue().frequency + entry.getValue().delta < currentBucketId)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        traceIdMap = traceIdMap.entrySet().stream()
                .filter(entry -> entry.getValue().frequency + entry.getValue().delta < currentBucketId)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


        directlyFollowsMap = directlyFollowsMap.entrySet().stream()
                .filter(entry -> entry.getValue().frequency + entry.getValue().delta < currentBucketId)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        n++;
    }

    private class TraceIdMapEntry {

        public TraceIdMapEntry(String lastEvent, int frequency, int delta) {
            this.lastEvent = lastEvent;
            this.frequency = frequency;
            this.delta = delta;
        }

        private String lastEvent;
        private int frequency;
        private int delta;
    }

    private class FrequencyDeltaPair {
        public FrequencyDeltaPair(int frequency, int delta) {
            this.frequency = frequency;
            this.delta = delta;
        }

        private int frequency;
        private int delta;
    }

}
