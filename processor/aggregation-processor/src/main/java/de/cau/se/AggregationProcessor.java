package de.cau.se;

import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Event;
import de.cau.se.map.directlyfollows.DirectlyFollowsRelationCountMap;
import de.cau.se.map.trace.TraceIdMap;
import de.cau.se.model.MinedProcessModel;
import de.cau.se.model.ModelUpdateService;
import de.cau.se.processmodel.ProcessModel;

/**
 * The aggregation processor mines parts of a process mining model. These parts are sent to the next stage.
 */
public class AggregationProcessor extends AbstractProcessor<Event, MinedProcessModel> {

    private final DirectlyFollowsRelationCountMap directlyFollowsCountMap;

    private final TraceIdMap traceIdEventMap;

    private final Integer bucketSize;

    private MinedProcessModel processModel;

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
        processModel = new MinedProcessModel();
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
        directlyFollowsCountMap.clear();
    }

    private void processBucket() {
        directlyFollowsCountMap.keySet().forEach(relation -> modelUpdateService.update(processModel, relation));
        sendResults();
    }

    private void sendResults() {
        super.send(processModel);
    }
}
