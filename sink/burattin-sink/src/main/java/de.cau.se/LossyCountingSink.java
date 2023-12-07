package de.cau.se;

import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Event;
import de.cau.se.map.result.BurattinResultMap;
import de.cau.se.map.result.FrequencyDeltaPair;
import de.cau.se.map.result.IResultMap;
import de.cau.se.map.trace.BurattinTraceIdMap;
import de.cau.se.map.trace.TraceIdMapEntry;
import de.cau.se.model.EventRelationLogger;
import de.cau.se.model.MinedProcessModel;
import de.cau.se.model.ModelUpdater;
import de.cau.se.model.PrecisionChecker;
import de.cau.se.processmodel.ProcessModel;
import org.apache.kafka.clients.consumer.Consumer;

import java.util.Optional;

/**
 * Heuristics Miner according to Andrea Burattin. Enhanced with usage of data structures.
 */
public class LossyCountingSink extends AbstractConsumer<Event> {
    int n = 1;
    private final IResultMap<String, FrequencyDeltaPair> activityMap = new BurattinResultMap<>();
    private final BurattinTraceIdMap traceIdMap = new BurattinTraceIdMap();
    private final IResultMap<DirectlyFollowsRelation, FrequencyDeltaPair> directlyFollowsMap = new BurattinResultMap<>();
    private final ModelUpdater modelUpdater;
    private final EventRelationLogger eventRelationLogger;
    private final PrecisionChecker precisionChecker;
    private final ProcessModel originalProcessModel;
    private final int bucketSize;
    private final int refreshRate;

    public LossyCountingSink(final Consumer<String, Event> consumer,
                             final int bucketSize,
                             final ModelUpdater modelUpdater,
                             final EventRelationLogger eventRelationLogger,
                             final PrecisionChecker precisionChecker,
                             final Integer refreshRate,
                             final ProcessModel originalProcessModel) {
        super(consumer);
        this.bucketSize = bucketSize;
        this.modelUpdater = modelUpdater;
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

        final TraceIdMapEntry traceIdMapEntry = traceIdMap.get(traceId);
        traceIdMap.insertOrUpdate(traceId, event.getActivity(), currentBucketId - 1);

        if (traceIdMapEntry != null && traceIdMapEntry.getLastEvent() != null) {
            final DirectlyFollowsRelation newDirectlyFollowsRelation = new DirectlyFollowsRelation(traceIdMapEntry.getLastEvent(), event.getActivity());
            directlyFollowsMap.insertOrUpdate(newDirectlyFollowsRelation, currentBucketId - 1);
        }
    }

    private void cleanupSets(int currentBucketId) {
        if (n % bucketSize == 0) {
            activityMap.removeIrrelevant(currentBucketId);
            traceIdMap.removeIrrelevantRelations(currentBucketId);
            directlyFollowsMap.removeIrrelevant(currentBucketId);
        }
    }

    private void performFullModelUpdate() {
        if (n % refreshRate == 0) {
            modelUpdater.update(directlyFollowsMap, activityMap.keySet());
            MinedProcessModel minedProcessModel = modelUpdater.getProcessModel();
            eventRelationLogger.logRelations(minedProcessModel);
            precisionChecker.calculatePrecision(originalProcessModel, minedProcessModel);
        }
    }

}
