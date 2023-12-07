package de.cau.se;

import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Event;
import de.cau.se.map.directlyfollows.DirectlyFollowsRelationCountMap;
import de.cau.se.map.result.ResultMap;
import de.cau.se.map.trace.TraceIdMap;
import de.cau.se.model.MinedProcessModel;
import de.cau.se.model.ModelUpdater;

/**
 * The aggregation processor mines parts of a process mining model. These parts are sent to the next stage.
 */
public class AggregationProcessor extends AbstractProcessor<Event, MinedProcessModel> {

    private final DirectlyFollowsRelationCountMap directlyFollowsCountMap;

    private final TraceIdMap traceIdEventMap;

    private final Integer bucketSize;

    private final ResultMap resultMap;

    private final ModelUpdater modelUpdater;

    private final boolean isBatchProcessing = true;

    public AggregationProcessor(final AbstractProducer<MinedProcessModel> sender,
                                final KafkaConsumer<Event> consumer,
                                final DirectlyFollowsRelationCountMap directlyFollowsCountMap,
                                final TraceIdMap traceIdEventMap,
                                final Integer bucketSize,
                                final ResultMap resultMap,
                                final ModelUpdater modelUpdater) {
        super(sender, consumer);
        this.directlyFollowsCountMap = directlyFollowsCountMap;
        this.traceIdEventMap = traceIdEventMap;
        this.bucketSize = bucketSize;
        this.resultMap = resultMap;
        this.modelUpdater = modelUpdater;
    }

    @Override
    public void receive(Event event) {
        updateTraceIdAndDirectlyFollowsMap(event);

        if (!isBatchProcessing || directlyFollowsCountMap.size() >= bucketSize) {
            processBucket();
            sendResults();
        }

        if (directlyFollowsCountMap.size() >= bucketSize) {
            clearBucket();
        }
    }

    private void updateTraceIdAndDirectlyFollowsMap(final Event event) {
        final String lastActivity = traceIdEventMap.accept(event.getTraceId(), event.getActivity());
        if (lastActivity != null) {
            directlyFollowsCountMap.accept(new DirectlyFollowsRelation(lastActivity, event.getActivity()));
        }
    }

    private void clearBucket() {
        System.out.println("Bucket filled. Clearing directly follows map");
        directlyFollowsCountMap.clear();
    }

    private void processBucket() {
        directlyFollowsCountMap.forEach((a, b) -> modelUpdater.update(a));
        sendResults();
    }

    private void sendResults() {
        super.send(modelUpdater.getProcessModel());
        System.out.println("Sending results");
    }
}
