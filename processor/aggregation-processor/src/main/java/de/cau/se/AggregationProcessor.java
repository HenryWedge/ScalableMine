package de.cau.se;

import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Event;
import de.cau.se.map.directlyfollows.DirectlyFollowsRelationCountMap;
import de.cau.se.map.trace.TraceIdMap;
import de.cau.se.model.MinedProcessModel;
import de.cau.se.model.ModelUpdateService;

/**
 * The aggregation processor mines parts of a process mining model. These parts are sent to the next stage.
 */
public class AggregationProcessor extends AbstractProcessor<Event, MinedProcessModel> {

    private final DirectlyFollowsRelationCountMap directlyFollowsCountMap;

    private final TraceIdMap traceIdEventMap;

    private final Integer bucketSize;

    private final ModelUpdateService modelUpdateService;

    public AggregationProcessor(final AbstractProducer<MinedProcessModel> sender,
                                final KafkaConsumer<Event> consumer,
                                final DirectlyFollowsRelationCountMap directlyFollowsCountMap,
                                final TraceIdMap traceIdEventMap,
                                final Integer bucketSize,
                                final ModelUpdateService modelUpdateService) {
        super(sender, consumer);
        this.directlyFollowsCountMap = directlyFollowsCountMap;
        this.traceIdEventMap = traceIdEventMap;
        this.bucketSize = bucketSize;
        this.modelUpdateService = modelUpdateService;
    }

    @Override
    public void receive(Event event) {
        updateTraceIdAndDirectlyFollowsMap(event);

        if (directlyFollowsCountMap.size() >= bucketSize) {
            processBucket();
            sendResults();
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
        directlyFollowsCountMap.forEach((a, b) -> modelUpdateService.update(a));
        sendResults();
    }

    private void sendResults() {
        super.send(modelUpdateService.getProcessModel());
        System.out.println("Sending results");
    }
}
