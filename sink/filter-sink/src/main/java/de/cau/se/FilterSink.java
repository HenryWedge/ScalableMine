package de.cau.se;

import de.cau.se.model.*;
import de.cau.se.processmodel.ProcessModel;
import org.apache.kafka.clients.consumer.Consumer;

public class FilterSink extends AbstractConsumer<MinedProcessModel> {

    private final EventRelationLogger eventRelationLogger;
    private final CountBasedMinedProcessModel minedProcessModel;
    private final Integer relevanceThreshold;
    private final Integer aggregateCount;
    private Integer receivedEvents = 0;
    private final PrecisionChecker precisionChecker;
    private final ProcessModel processModel;

    public FilterSink(final Consumer<String, MinedProcessModel> consumer,
                      final Integer relevanceThreshold,
                      final Integer aggregateCount,
                      final CountBasedMinedProcessModel minedProcessModel,
                      final EventRelationLogger eventRelationLogger,
                      final PrecisionChecker precisionChecker,
                      final ProcessModel processModel) {
        super(consumer);
        this.relevanceThreshold = relevanceThreshold;
        this.aggregateCount = aggregateCount;
        this.eventRelationLogger = eventRelationLogger;
        this.precisionChecker = precisionChecker;
        this.processModel = processModel;
        this.minedProcessModel = minedProcessModel;
    }

    @Override
    public void receive(final MinedProcessModel receivingProcessModel) {
        if (receivingProcessModel == null) {
            return;
        }

        receivingProcessModel.getCausalEvents().forEach(minedProcessModel::addCausalEvent);
        receivingProcessModel.getParallelGateways().forEach(minedProcessModel::addAndGateway);
        receivingProcessModel.getChoiceGateways().forEach(minedProcessModel::addXorGateway);

        if (receivedEvents % aggregateCount == 0) {
            minedProcessModel.removeGatewaysAndDirectlyFollowsRelationsBelowRelevanceThreshold(relevanceThreshold);
            eventRelationLogger.logRelations(minedProcessModel);
            precisionChecker.calculatePrecision(processModel, minedProcessModel);
        }

        receivedEvents++;
    }
}