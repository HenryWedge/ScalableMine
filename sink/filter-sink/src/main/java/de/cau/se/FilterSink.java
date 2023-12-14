package de.cau.se;

import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Gateway;
import de.cau.se.map.result.LossyCountingRelationCountMap;
import de.cau.se.map.result.RelationCountMap;
import de.cau.se.map.result.MicroBatchRelationCountMap;
import de.cau.se.model.*;
import de.cau.se.processmodel.ProcessModel;
import org.apache.kafka.clients.consumer.Consumer;

import java.util.Set;

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

        updateStoredEventFromReceivingEvent(receivingProcessModel.getCausalEvents(), causalEvents);
        updateStoredEventFromReceivingEvent(receivingProcessModel.getParallelGateways(), parallelGateways);
        updateStoredEventFromReceivingEvent(receivingProcessModel.getChoiceGateways(), choiceGateways);

        if (receivedEvents % refreshRate == 0) {
            updateModelFromCollectedPattern(causalEvents, minedProcessModel.getCausalEventMap());
            updateModelFromCollectedPattern(parallelGateways, minedProcessModel.getAndGateways());
            updateModelFromCollectedPattern(choiceGateways, minedProcessModel.getXorGateways());

            eventRelationLogger.logRelations(minedProcessModel);
            precisionChecker.calculatePrecision(processModel, minedProcessModel);
        }

        receivedEvents++;
    }

    private <T> void updateModelFromCollectedPattern(final MicroBatchRelationCountMap<T> storedRelation, final MicroBatchRelationCountMap<T> modelRelation) {
        storedRelation.getIrrelevantRelations(irrelevanceThreshold).forEach(modelRelation::remove);
        storedRelation.getRelevantRelations(relevanceThreshold).forEach(pattern -> modelRelation.insertOrUpdate(pattern, 1));
        storedRelation.clear();
    }

    private <T> void updateStoredEventFromReceivingEvent(final Set<T> receivedRelation, final MicroBatchRelationCountMap<T> storedRelation) {
        receivedRelation.forEach(pattern -> storedRelation.insertOrUpdate(pattern, 1));
    }
}