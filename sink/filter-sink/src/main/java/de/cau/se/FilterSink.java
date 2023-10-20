package de.cau.se;

import de.cau.se.datastructure.DirectlyFollows;
import de.cau.se.datastructure.Gateway;
import de.cau.se.datastructure.Result;
import de.cau.se.map.ResultMap;
import de.cau.se.model.EventRelationLogger;
import de.cau.se.model.ModelUpdater;
import de.cau.se.model.PrecisionChecker;
import org.apache.kafka.clients.consumer.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class FilterSink extends AbstractConsumer<Result> {
    private static final Logger log = LoggerFactory.getLogger(FilterSink.class);
    private final Set<DirectlyFollows> causalEvents;
    private final Set<Gateway> parallelGateways;
    private final Set<Gateway> xorGateways;
    private final Double andThreshold;
    private final Double dependencyThreshold;
    private final ResultMap resultMap;
    private final ModelUpdater modelUpdater;
    private final EventRelationLogger eventRelationLogger;
    private final Set<String> frequentActivities = new HashSet<>();
    private final Integer relevanceThreshold;
    private final Integer aggregateCount;
    private AtomicInteger receivedEvents = new AtomicInteger(0);

    private final PrecisionChecker precisionChecker;

    public FilterSink(final Consumer<String, Result> consumer,
                           final Integer relevanceThreshold,
                           final Integer aggregateCount,
                           final Double andThreshold,
                           final Double dependencyThreshold,
                           final Set<DirectlyFollows> causalEvents,
                           final Set<Gateway> parallelEvents,
                           final Set<Gateway> xorEvents,
                           final ResultMap resultMap,
                           final ModelUpdater modelUpdater,
                           final EventRelationLogger eventRelationLogger,
                           final PrecisionChecker precisionChecker) {
        super(consumer);
        this.relevanceThreshold = relevanceThreshold;
        this.aggregateCount = aggregateCount;
        this.andThreshold = andThreshold;
        this.dependencyThreshold = dependencyThreshold;
        this.causalEvents = causalEvents;
        this.parallelGateways = parallelEvents;
        this.xorGateways = xorEvents;
        this.resultMap = resultMap;
        this.modelUpdater = modelUpdater;
        this.eventRelationLogger = eventRelationLogger;
        this.precisionChecker = precisionChecker;
    }

    @Override
    public void receive(final Result result) {
        System.out.println("Value: " + result.toString());
        frequentActivities.add(result.getDirectlyFollows().getSuccessor());
        frequentActivities.add(result.getDirectlyFollows().getPredecessor());
        resultMap.accept(result);

        if (receivedEvents.incrementAndGet() % aggregateCount == 0) {
            resultMap.removeIrrelevantEvents(relevanceThreshold);
            modelUpdater.findCausalEvents(causalEvents, resultMap, dependencyThreshold, result.getDirectlyFollows());
            modelUpdater.findSplits(frequentActivities, causalEvents, resultMap, andThreshold, parallelGateways, xorGateways);
            modelUpdater.findJoins(frequentActivities, causalEvents, resultMap, andThreshold, parallelGateways, xorGateways);
            eventRelationLogger.logRelations(causalEvents, parallelGateways, xorGateways);
            precisionChecker.calculatePrecision(causalEvents, parallelGateways, xorGateways);
        }
    }
}
