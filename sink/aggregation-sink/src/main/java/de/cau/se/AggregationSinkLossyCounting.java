package de.cau.se;

import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Result;
import de.cau.se.map.result.MicroBatchRelationCountMap;
import de.cau.se.model.EventRelationLogger;
import de.cau.se.model.ModelUpdateService;
import de.cau.se.model.PrecisionChecker;
import de.cau.se.processmodel.ProcessModel;
import org.apache.kafka.clients.consumer.Consumer;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class AggregationSinkLossyCounting extends AbstractConsumer<Result> {

    private final MicroBatchRelationCountMap<DirectlyFollowsRelation> relationCountMap;

    private final ModelUpdateService modelUpdateService;

    private final PrecisionChecker precisionChecker;

    private final Integer refreshRate;

    private final Integer relevanceThreshold;

    private int n = 0;

    private final ProcessModel processModel;

    public AggregationSinkLossyCounting(final Consumer<String, Result> consumer,
                                        final MicroBatchRelationCountMap<DirectlyFollowsRelation> microBatchRelationCountMap,
                                        final int refreshRate,
                                        final int relevanceThreshold,
                                        final ModelUpdateService modelUpdateService,
                                        final EventRelationLogger eventRelationLogger,
                                        final PrecisionChecker precisionChecker,
                                        final ProcessModel processModel) {
        super(consumer);
        this.relationCountMap = microBatchRelationCountMap;
        this.refreshRate = refreshRate;
        this.relevanceThreshold = relevanceThreshold;
        this.modelUpdateService = modelUpdateService;
        this.eventRelationLogger = eventRelationLogger;
        this.precisionChecker = precisionChecker;
        this.processModel = processModel;
    }

    private final EventRelationLogger eventRelationLogger;

    @Override
    public void receive(final Result result) {
        n++;
        relationCountMap.insertOrUpdate(result.getDirectlyFollows(), result.getCount());

        final Set<String> frequentActivities = relationCountMap
                .keySet()
                .stream()
                .map(directlyFollowsRelation ->
                        Set.of(directlyFollowsRelation.getPredecessor(),
                               directlyFollowsRelation.getPredecessor()))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        if (n % refreshRate == 0) {
            relationCountMap.removeIrrelevant(relevanceThreshold);
            modelUpdateService.update(relationCountMap, frequentActivities);
            eventRelationLogger.logRelations(processModel);
            precisionChecker.calculatePrecision(processModel, modelUpdateService.getProcessModel());
            relationCountMap.clear();
        }
    }
}