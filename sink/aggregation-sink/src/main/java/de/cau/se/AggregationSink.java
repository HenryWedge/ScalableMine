package de.cau.se;

import de.cau.se.datastructure.Result;
import de.cau.se.map.ResultMap;
import de.cau.se.model.EventRelationLogger;
import de.cau.se.model.ModelUpdater;
import de.cau.se.model.PrecisionChecker;
import de.cau.se.processmodel.ProcessModel;
import de.cau.se.processmodel.SmallProcessModel;
import org.apache.kafka.clients.consumer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AggregationSink extends AbstractConsumer<Result> {
    private static final Logger log = LoggerFactory.getLogger(AggregationSink.class);
    private final ResultMap resultMap;

    private final ModelUpdater modelUpdater;

    private final PrecisionChecker precisionChecker;

    private final ProcessModel processModel;

    public AggregationSink(final Consumer<String, Result> consumer,
                           final ResultMap resultMap,
                           final ModelUpdater modelUpdater,
                           final EventRelationLogger eventRelationLogger,
                           final PrecisionChecker precisionChecker,
                           final ProcessModel processModel) {
        super(consumer);
        this.resultMap = resultMap;
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

        modelUpdater.findCausalEvents(resultMap, result.getDirectlyFollows());
        modelUpdater.findSplits(Set.of(result.getDirectlyFollows().getSuccessor(), result.getDirectlyFollows().getPredecessor()), resultMap);
        modelUpdater.findJoins(Set.of(result.getDirectlyFollows().getSuccessor(), result.getDirectlyFollows().getPredecessor()), resultMap);
        eventRelationLogger.logRelations(processModel);
        precisionChecker.calculatePrecision(processModel, modelUpdater.getProcessModel());
    }


}
