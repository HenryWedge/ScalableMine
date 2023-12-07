package de.cau.se;

import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Gateway;
import de.cau.se.map.result.RelationCountMap;
import de.cau.se.map.result.MicroBatchRelationCountMap;
import de.cau.se.model.*;
import de.cau.se.processmodel.ProcessModel;
import org.apache.kafka.clients.consumer.Consumer;

public class FilterSink extends AbstractConsumer<MinedProcessModel> {

    private final EventRelationLogger eventRelationLogger;
    private final CountBasedMinedProcessModel minedProcessModel;
    private final Integer relevanceThreshold;
    private final Integer irrelevanceThreshold;
    private final Integer refreshRate;
    private Integer receivedEvents = 0;
    private final PrecisionChecker precisionChecker;
    private final ProcessModel processModel;
    private final MicroBatchRelationCountMap<DirectlyFollowsRelation> causalEvents = new MicroBatchRelationCountMap<>();
    private final MicroBatchRelationCountMap<Gateway> parallelGateways = new MicroBatchRelationCountMap<>();
    private final MicroBatchRelationCountMap<Gateway> choiceGateways = new MicroBatchRelationCountMap<>();

    public FilterSink(final Consumer<String, MinedProcessModel> consumer,
                      final Integer relevanceThreshold,
                      final Integer irrelevanceThreshold,
                      final Integer refreshRate,
                      final CountBasedMinedProcessModel minedProcessModel,
                      final EventRelationLogger eventRelationLogger,
                      final PrecisionChecker precisionChecker,
                      final ProcessModel processModel) {
        super(consumer);
        this.relevanceThreshold = relevanceThreshold;
        this.irrelevanceThreshold = irrelevanceThreshold;
        this.refreshRate = refreshRate;
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

        receivingProcessModel.getCausalEvents().forEach(causalEvent -> causalEvents.insertOrUpdate(causalEvent, 1));
        receivingProcessModel.getParallelGateways().forEach(parallelGateway -> parallelGateways.insertOrUpdate(parallelGateway, 1));
        receivingProcessModel.getChoiceGateways().forEach(choiceGateway -> choiceGateways.insertOrUpdate(choiceGateway, 1));

        if (receivedEvents % refreshRate == 0) {
            causalEvents.getIrrelevantRelations(irrelevanceThreshold).forEach(minedProcessModel.getCausalEventMap()::remove);
            parallelGateways.getIrrelevantRelations(irrelevanceThreshold).forEach(minedProcessModel.getAndGateways()::remove);
            choiceGateways.getIrrelevantRelations(irrelevanceThreshold).forEach(minedProcessModel.getXorGateways()::remove);

            causalEvents.getRelevantRelations(relevanceThreshold).forEach(minedProcessModel.getCausalEvents()::add);
            parallelGateways.getRelevantRelations(relevanceThreshold).forEach(gateway -> minedProcessModel.getAndGateways().put(gateway, 1));
            choiceGateways.getRelevantRelations(relevanceThreshold).forEach(gateway -> minedProcessModel.getXorGateways().put(gateway, 1));

            causalEvents.clear();
            parallelGateways.clear();
            choiceGateways.clear();

            eventRelationLogger.logRelations(minedProcessModel);
            precisionChecker.calculatePrecision(processModel, minedProcessModel);
        }

        receivedEvents++;
    }
}