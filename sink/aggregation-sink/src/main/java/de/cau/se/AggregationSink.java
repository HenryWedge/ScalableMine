package de.cau.se;

import de.cau.se.datastructure.Result;
import de.cau.se.map.result.ResultMap;
import de.cau.se.model.EventRelationLogger;
import de.cau.se.model.ModelUpdater;
import de.cau.se.model.PrecisionChecker;
import de.cau.se.processmodel.ProcessModel;
import org.apache.kafka.clients.consumer.Consumer;
import java.util.concurrent.atomic.AtomicInteger;

public class AggregationSink extends AbstractConsumer<Result> {

    private final ResultMap resultMap;

    private final ModelUpdater modelUpdater;

    private final PrecisionChecker precisionChecker;

    private final Integer aggregateCount;

    private final Integer relevanceThreshold;

    private final AtomicInteger receivedEvents = new AtomicInteger(0);

    private final ProcessModel processModel;

    public AggregationSink(final Consumer<String, Result> consumer,
                           final ResultMap resultMap,
                           final int aggregateCount,
                           final int relevanceThreshold,
                           final ModelUpdater modelUpdater,
                           final EventRelationLogger eventRelationLogger,
                           final PrecisionChecker precisionChecker,
                           final ProcessModel processModel) {
        super(consumer);
        this.resultMap = resultMap;
        this.aggregateCount = aggregateCount;
        this.relevanceThreshold = relevanceThreshold;
        this.modelUpdater = modelUpdater;
        this.eventRelationLogger = eventRelationLogger;
        this.precisionChecker = precisionChecker;
        this.processModel = processModel;
    }

    private final EventRelationLogger eventRelationLogger;

    @Override
    public void receive(final Result result) {
        resultMap.insertOrUpdate(result.getDirectlyFollows(), result.getCount());

        if (receivedEvents.incrementAndGet() % aggregateCount == 0) {
            resultMap.removeIrrelevant(relevanceThreshold);
            modelUpdater.update(result.getDirectlyFollows(), result.getCount());
            eventRelationLogger.logRelations(processModel);
            precisionChecker.calculatePrecision(processModel, modelUpdater.getProcessModel());
        }
    }
}
