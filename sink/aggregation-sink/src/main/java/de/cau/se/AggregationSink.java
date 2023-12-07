package de.cau.se;

import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Result;
import de.cau.se.map.result.MicroBatchRelationCountMap;
import de.cau.se.model.EventRelationLogger;
import de.cau.se.model.ModelUpdateService;
import de.cau.se.model.PrecisionChecker;
import de.cau.se.processmodel.ProcessModel;
import org.apache.kafka.clients.consumer.Consumer;

public class AggregationSink extends AbstractConsumer<Result> {

    private final MicroBatchRelationCountMap<DirectlyFollowsRelation> microBatchRelationCountMap;

    private final ModelUpdateService modelUpdateService;

    private final PrecisionChecker precisionChecker;

    private final Integer refreshRate;

    private final Integer relevanceThreshold;

    private final Integer irrelevanceThreshold;

    private int n = 0;

    private final ProcessModel processModel;

    public AggregationSink(final Consumer<String, Result> consumer,
                           final MicroBatchRelationCountMap<DirectlyFollowsRelation> microBatchRelationCountMap,
                           final int refreshRate,
                           final int relevanceThreshold,
                           final int irrelevanceThreshold,
                           final ModelUpdateService modelUpdateService,
                           final EventRelationLogger eventRelationLogger,
                           final PrecisionChecker precisionChecker,
                           final ProcessModel processModel) {
        super(consumer);
        this.microBatchRelationCountMap = microBatchRelationCountMap;
        this.refreshRate = refreshRate;
        this.relevanceThreshold = relevanceThreshold;
        this.irrelevanceThreshold = irrelevanceThreshold;
        this.modelUpdateService = modelUpdateService;
        this.eventRelationLogger = eventRelationLogger;
        this.precisionChecker = precisionChecker;
        this.processModel = processModel;
    }

    private final EventRelationLogger eventRelationLogger;

    @Override
    public void receive(final Result result) {
        n++;
        microBatchRelationCountMap.insertOrUpdate(result.getDirectlyFollows(), result.getCount());

        if (n % refreshRate == 0) {
            microBatchRelationCountMap.getRelevantRelations(relevanceThreshold).forEach(modelUpdateService::update);
            microBatchRelationCountMap.getIrrelevantRelations(irrelevanceThreshold).forEach(modelUpdateService::remove);
            microBatchRelationCountMap.clear();

            modelUpdateService.update(result.getDirectlyFollows(), result.getCount());
            eventRelationLogger.logRelations(processModel);
            precisionChecker.calculatePrecision(processModel, modelUpdateService.getProcessModel());
        }
    }
}
