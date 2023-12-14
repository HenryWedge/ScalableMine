package de.cau.se;

import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Result;
import de.cau.se.map.result.MicroBatchRelationCountMap;
import de.cau.se.model.EventRelationLogger;
import de.cau.se.model.MinedProcessModel;
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

    private int n = 0;
    private final ProcessModel originalProcessModel;
    private final MinedProcessModel processModel;

    public AggregationSinkLossyCounting(final Consumer<String, Result> consumer,
                                        final MicroBatchRelationCountMap<DirectlyFollowsRelation> microBatchRelationCountMap,
                                        final int refreshRate,
                                        final ModelUpdateService modelUpdateService,
                                        final EventRelationLogger eventRelationLogger,
                                        final PrecisionChecker precisionChecker,
                                        final ProcessModel originalProcessModel) {
        super(consumer);
        this.relationCountMap = microBatchRelationCountMap;
        this.refreshRate = refreshRate;
        this.modelUpdateService = modelUpdateService;
        this.eventRelationLogger = eventRelationLogger;
        this.precisionChecker = precisionChecker;
        this.originalProcessModel = originalProcessModel;
        this.processModel = new MinedProcessModel();
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
                               directlyFollowsRelation.getSuccessor()))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        if (n % refreshRate == 0) {
            modelUpdateService.updateProcessModel(processModel, relationCountMap, frequentActivities);
            eventRelationLogger.logRelations(processModel);
            precisionChecker.calculatePrecision(originalProcessModel, processModel);
            relationCountMap.clear();
        }
    }
}