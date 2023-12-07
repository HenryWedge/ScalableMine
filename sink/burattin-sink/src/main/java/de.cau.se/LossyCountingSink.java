package de.cau.se;

import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Event;
import de.cau.se.map.result.LossyCountingRelationCountMap;
import de.cau.se.map.trace.LossyCountingTraceIdMap;
import de.cau.se.model.EventRelationLogger;
import de.cau.se.model.MinedProcessModel;
import de.cau.se.model.ModelUpdateService;
import de.cau.se.model.PrecisionChecker;
import de.cau.se.processmodel.ProcessModel;
import org.apache.kafka.clients.consumer.Consumer;

/**
 * Heuristics Miner according to Andrea Burattin. Enhanced with usage of data structures.
 */
public class LossyCountingSink extends AbstractConsumer<Event> {
    int n = 1;
    private final LossyCountingRelationCountMap<String> activityMap = new LossyCountingRelationCountMap<>();
    private final LossyCountingTraceIdMap traceIdMap = new LossyCountingTraceIdMap();
    private final LossyCountingRelationCountMap<DirectlyFollowsRelation> relationCountMap = new LossyCountingRelationCountMap<>();
    private final ModelUpdateService modelUpdateService;
    private final EventRelationLogger eventRelationLogger;
    private final PrecisionChecker precisionChecker;
    private final ProcessModel originalProcessModel;
    private final int bucketSize;
    private final int refreshRate;

    public LossyCountingSink(final Consumer<String, Event> consumer,
                             final int bucketSize,
                             final ModelUpdateService modelUpdateService,
                             final EventRelationLogger eventRelationLogger,
                             final PrecisionChecker precisionChecker,
                             final Integer refreshRate,
                             final ProcessModel originalProcessModel) {
        super(consumer);
        this.bucketSize = bucketSize;
        this.modelUpdateService = modelUpdateService;
        this.eventRelationLogger = eventRelationLogger;
        this.precisionChecker = precisionChecker;
        this.refreshRate = refreshRate;
        this.originalProcessModel = originalProcessModel;
    }

    @Override
    public void receive(Event event) {
        final int currentBucketId = (int) Math.ceil((double) n / bucketSize);
        updateSets(event, currentBucketId);
        cleanupSets(currentBucketId);
        n++;
        performFullModelUpdate();
    }

    private void updateSets(Event event, int currentBucketId) {
        final String activity = event.getActivity();
        final int traceId = event.getTraceId();

        activityMap.insertOrUpdate(activity, currentBucketId - 1);

        final LossyCountingTraceIdMap.Entry entry = traceIdMap.get(traceId);
        traceIdMap.insertOrUpdate(traceId, event.getActivity(), currentBucketId - 1);

        if (entry != null && entry.getLastEvent() != null) {
            final DirectlyFollowsRelation newDirectlyFollowsRelation = new DirectlyFollowsRelation(entry.getLastEvent(), event.getActivity());
            relationCountMap.insertOrUpdate(newDirectlyFollowsRelation, currentBucketId - 1);
        }
    }

    private void cleanupSets(int currentBucketId) {
        if (n % bucketSize == 0) {
            activityMap.removeIrrelevant(currentBucketId);
            traceIdMap.removeIrrelevantRelations(currentBucketId);
            relationCountMap.removeIrrelevant(currentBucketId);
        }
    }

    private void performFullModelUpdate() {
        if (n % refreshRate == 0) {
            modelUpdateService.update(relationCountMap, activityMap.keySet());
            MinedProcessModel minedProcessModel = modelUpdateService.getProcessModel();
            eventRelationLogger.logRelations(minedProcessModel);
            precisionChecker.calculatePrecision(originalProcessModel, minedProcessModel);
        }
    }

}
