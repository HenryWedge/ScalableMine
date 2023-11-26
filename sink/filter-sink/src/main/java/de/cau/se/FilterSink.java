package de.cau.se;

import de.cau.se.datastructure.DirectlyFollows;
import de.cau.se.datastructure.Gateway;
import de.cau.se.datastructure.Result;
import de.cau.se.map.ResultMap;
import de.cau.se.model.EventRelationLogger;
import de.cau.se.model.ModelUpdater;
import de.cau.se.model.PrecisionChecker;
import de.cau.se.processmodel.ProcessModel;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class FilterSink extends AbstractConsumer<Result> {
    private static final Logger log = LoggerFactory.getLogger(FilterSink.class);
    private final ResultMap resultMap;
    private final ModelUpdater modelUpdater;
    private final EventRelationLogger eventRelationLogger;
    private final Set<String> frequentActivities = new HashSet<>();

    private final Set<DirectlyFollows> newDirectlyFollowsBuffer;

    private final Integer relevanceThreshold;
    private final Integer aggregateCount;
    private final AtomicInteger receivedEvents = new AtomicInteger(0);

    private final PrecisionChecker precisionChecker;

    private final ProcessModel processModel;

    public FilterSink(final Consumer<String, Result> consumer,
                      final Integer relevanceThreshold,
                      final Integer aggregateCount,
                      final Set<DirectlyFollows> newDirectlyFollowsBuffer,
                      final ResultMap resultMap,
                      final ModelUpdater modelUpdater,
                      final EventRelationLogger eventRelationLogger,
                      final PrecisionChecker precisionChecker,
                      final ProcessModel processModel) {
        super(consumer);
        this.relevanceThreshold = relevanceThreshold;
        this.aggregateCount = aggregateCount;
        this.newDirectlyFollowsBuffer = newDirectlyFollowsBuffer;
        this.resultMap = resultMap;
        this.modelUpdater = modelUpdater;
        this.eventRelationLogger = eventRelationLogger;
        this.precisionChecker = precisionChecker;
        this.processModel = processModel;
    }

    @Override
    public void receive(final Result result) {
        if (result == null) {
            return;
        }
        System.out.println("Value: " + result);
        frequentActivities.add(result.getDirectlyFollows().getSuccessor());
        frequentActivities.add(result.getDirectlyFollows().getPredecessor());
        resultMap.accept(result);
        newDirectlyFollowsBuffer.add(result.getDirectlyFollows());

        if (receivedEvents.incrementAndGet() % aggregateCount == 0) {
            resultMap.removeIrrelevantEvents(relevanceThreshold);
            newDirectlyFollowsBuffer.forEach(df -> modelUpdater.findCausalEvents(resultMap, df));
            modelUpdater.findSplits(frequentActivities, resultMap);
            modelUpdater.findJoins(frequentActivities, resultMap);
            eventRelationLogger.logRelations(modelUpdater.getProcessModel());
            precisionChecker.calculatePrecision(processModel, modelUpdater.getProcessModel());
            newDirectlyFollowsBuffer.clear();
        }
    }
}
