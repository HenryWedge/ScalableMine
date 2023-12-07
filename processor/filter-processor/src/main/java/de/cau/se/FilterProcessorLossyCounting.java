package de.cau.se;

import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Event;
import de.cau.se.datastructure.Result;
import de.cau.se.map.result.LossyCountingRelationCountMap;
import de.cau.se.map.trace.LossyCountingTraceIdMap;
import org.apache.kafka.clients.consumer.Consumer;

public class FilterProcessorLossyCounting extends AbstractProcessor<Event, Result> {

    int n = 1;
    private final LossyCountingTraceIdMap traceIdMap = new LossyCountingTraceIdMap();
    private final LossyCountingRelationCountMap<DirectlyFollowsRelation> directlyFollowsMap = new LossyCountingRelationCountMap<>();
    private final int bucketSize;

    public FilterProcessorLossyCounting(final AbstractProducer<Result> sender,
                                        final Consumer<String, Event> consumer,
                                        final int bucketSize) {
        super(sender, consumer);
        this.bucketSize = bucketSize;
    }

    @Override
    public void receive(Event event) {
        final int currentBucketId = (int) Math.ceil((double) n / bucketSize);
        updateSets(event, currentBucketId);
        cleanupSets(currentBucketId);
        sendResults();
        n++;
    }

    private void updateSets(Event event, int currentBucketId) {
        final int traceId = event.getTraceId();

        final LossyCountingTraceIdMap.Entry entry = traceIdMap.get(traceId);
        traceIdMap.insertOrUpdate(traceId, event.getActivity(), currentBucketId - 1);

        if (entry != null && entry.getLastEvent() != null) {
            final DirectlyFollowsRelation newDirectlyFollowsRelation =
                    new DirectlyFollowsRelation(entry.getLastEvent(), event.getActivity());
            directlyFollowsMap.insertOrUpdate(newDirectlyFollowsRelation, currentBucketId - 1);
        }
    }

    private void cleanupSets(final int currentBucketId) {
        if (n % bucketSize == 0) {
            traceIdMap.removeIrrelevantRelations(currentBucketId);
            directlyFollowsMap.removeIrrelevant(currentBucketId);
        }
    }

    private void sendResults() {
        if (n % bucketSize == 0) {
            directlyFollowsMap
                .entrySet()
                .stream()
                .map(entry -> new Result(entry.getKey(), entry.getValue().getFrequency()))
                .forEach(super::send);
        }
    }
}
