package de.cau.se;

import de.cau.se.datastructure.Result;
import de.cau.se.map.ResultMap;
import de.cau.se.model.EventRelationLogger;
import de.cau.se.model.ModelUpdater;
import de.cau.se.model.PrecisionChecker;
import de.cau.se.processmodel.ProcessModel;
import org.apache.kafka.clients.consumer.Consumer;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class AggregationSink2 extends AbstractConsumer<Result> {

    private final ResultMap resultMap;

    private final ModelUpdater modelUpdater;

    private final PrecisionChecker precisionChecker;

    private final Integer aggregateCount;

    private final Integer relevanceThreshold;

    private final AtomicInteger receivedEvents = new AtomicInteger(0);

    private final ProcessModel processModel;

    public AggregationSink2(final Consumer<String, Result> consumer,
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
        System.out.println("Value: " + result.toString());
        resultMap.accept(result);

        if (receivedEvents.incrementAndGet() % aggregateCount == 0) {
            resultMap.removeIrrelevantEvents(relevanceThreshold);
            modelUpdater.findCausalEvents(resultMap, result.getDirectlyFollows());
            modelUpdater.findSplits(Set.of(result.getDirectlyFollows().getSuccessor(), result.getDirectlyFollows().getPredecessor()), resultMap);
            modelUpdater.findJoins(Set.of(result.getDirectlyFollows().getSuccessor(), result.getDirectlyFollows().getPredecessor()), resultMap);
            eventRelationLogger.logRelations(processModel);
            precisionChecker.calculatePrecision(processModel, modelUpdater.getProcessModel());
        }
    }
}
