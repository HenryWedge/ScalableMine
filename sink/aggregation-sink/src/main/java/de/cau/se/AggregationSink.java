package de.cau.se;

import de.cau.se.datastructure.DirectlyFollows;
import de.cau.se.datastructure.Gateway;
import de.cau.se.datastructure.Result;
import de.cau.se.map.ResultMap;
import de.cau.se.model.EventRelationLogger;
import de.cau.se.model.ModelUpdater;
import de.cau.se.model.PrecisionChecker;
import org.apache.kafka.clients.consumer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AggregationSink extends AbstractConsumer<Result> {
    private static final Logger log = LoggerFactory.getLogger(AggregationSink.class);
    private final Set<DirectlyFollows> causalEvents;
    private final Set<Gateway> parallelGateways;
    private final Set<Gateway> xorGateways;
    private final Double andThreshold;
    private final Double dependencyThreshold;
    private final ResultMap resultMap;

    private final ModelUpdater modelUpdater;

    private final Set<String> frequentActivities = new HashSet<>();

    private final PrecisionChecker precisionChecker;

    public AggregationSink(final Consumer<String, Result> consumer,
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

    private final EventRelationLogger eventRelationLogger;

    @Override
    public void receive(final Result result) {
        System.out.println("Value: " + result.toString());
        frequentActivities.add(result.getDirectlyFollows().getSuccessor());
        frequentActivities.add(result.getDirectlyFollows().getPredecessor());
        resultMap.accept(result);

        modelUpdater.findCausalEvents(causalEvents, resultMap, dependencyThreshold, result.getDirectlyFollows());
        modelUpdater.findSplits(frequentActivities, causalEvents, resultMap, andThreshold, parallelGateways, xorGateways);
        modelUpdater.findJoins(frequentActivities, causalEvents, resultMap, andThreshold, parallelGateways, xorGateways);
        eventRelationLogger.logRelations(causalEvents, parallelGateways, xorGateways);

        precisionChecker.calculatePrecision(causalEvents, parallelGateways, xorGateways);
    }


}
