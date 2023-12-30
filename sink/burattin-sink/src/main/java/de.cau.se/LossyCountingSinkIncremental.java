package de.cau.se;

import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Event;
import de.cau.se.map.result.CountBasedRelationCountMap;
import de.cau.se.map.trace.TraceIdMap;
import de.cau.se.model.EventRelationLogger;
import de.cau.se.model.MinedProcessModel;
import de.cau.se.model.ModelUpdateService;
import de.cau.se.model.PrecisionChecker;
import de.cau.se.processmodel.ProcessModel;
import org.apache.kafka.clients.consumer.Consumer;

import java.util.HashSet;
import java.util.Set;

/**
 * Heuristics Miner according to Andrea Burattin. Enhanced with usage of data structures.
 */
public class LossyCountingSinkIncremental extends AbstractConsumer<Event> {
    int n = 1;
    private final TraceIdMap traceIdMap = new TraceIdMap();
    private final CountBasedRelationCountMap<DirectlyFollowsRelation> directlyFollowsMap = new CountBasedRelationCountMap<>();
    private final ModelUpdateService modelUpdateService;
    private final EventRelationLogger eventRelationLogger;
    private final PrecisionChecker precisionChecker;
    private final ProcessModel originalProcessModel;
    private final MinedProcessModel processModel;
    private final Set<DirectlyFollowsRelation> irrelevantDirectlyFollowsRelations = new HashSet<>();
    private final Set<DirectlyFollowsRelation> relevantDirectlyFollowsRelations = new HashSet<>();
    private final int bucketSize;
    private final int refreshRate;
    private final int relevanceThreshold;
    private final int irrelevanceThreshold;

    public LossyCountingSinkIncremental(final Consumer<String, Event> consumer,
                                        final int bucketSize,
                                        final ModelUpdateService modelUpdateService,
                                        final EventRelationLogger eventRelationLogger,
                                        final PrecisionChecker precisionChecker,
                                        final Integer refreshRate,
                                        final Integer relevanceThreshold,
                                        final Integer irrelevanceThreshold,
                                        final ProcessModel originalProcessModel) {
        super(consumer);
        this.bucketSize = bucketSize;
        this.modelUpdateService = modelUpdateService;
        this.eventRelationLogger = eventRelationLogger;
        this.precisionChecker = precisionChecker;
        this.refreshRate = refreshRate;
        this.relevanceThreshold = relevanceThreshold;
        this.irrelevanceThreshold = irrelevanceThreshold;
        this.originalProcessModel = originalProcessModel;
        this.processModel = new MinedProcessModel();
    }

    @Override
    public void receive(Event event) {
        updateSets(event);
        cleanupSets();
        n++;
        performFullModelUpdate();
    }

    private void updateSets(Event event) {
        final String lastActivity = traceIdMap.get(event.getTraceId());
        traceIdMap.put(event.getTraceId(), event.getActivity());

        if (lastActivity != null) {
            final DirectlyFollowsRelation newDirectlyFollowsRelation = new DirectlyFollowsRelation(lastActivity, event.getActivity());
            directlyFollowsMap.insertOrUpdate(newDirectlyFollowsRelation, 1);
        }
    }

    private void cleanupSets() {
        if (n % bucketSize == 0) {
            irrelevantDirectlyFollowsRelations.addAll(directlyFollowsMap.getIrrelevantRelations(irrelevanceThreshold));
            relevantDirectlyFollowsRelations.addAll(directlyFollowsMap.getRelevantRelations(relevanceThreshold));
            directlyFollowsMap.clear();
        }
    }

    private void performFullModelUpdate() {
        if (n % refreshRate == 0) {
            relevantDirectlyFollowsRelations.forEach(relation -> modelUpdateService.update(processModel, relation));
            irrelevantDirectlyFollowsRelations.forEach(relation -> modelUpdateService.remove(processModel, relation));

            irrelevantDirectlyFollowsRelations.clear();
            relevantDirectlyFollowsRelations.clear();

            eventRelationLogger.logRelations(processModel);
            precisionChecker.calculatePrecision(originalProcessModel, processModel);
        }
    }

}
