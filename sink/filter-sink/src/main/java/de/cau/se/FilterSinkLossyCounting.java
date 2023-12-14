package de.cau.se;

import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Gateway;
import de.cau.se.map.result.LossyCountingRelationCountMap;
import de.cau.se.map.result.MicroBatchRelationCountMap;
import de.cau.se.model.CountBasedMinedProcessModel;
import de.cau.se.model.EventRelationLogger;
import de.cau.se.model.MinedProcessModel;
import de.cau.se.model.PrecisionChecker;
import de.cau.se.processmodel.ProcessModel;
import org.apache.kafka.clients.consumer.Consumer;

import java.util.Set;

public class FilterSinkLossyCounting extends AbstractConsumer<MinedProcessModel> {

    private final EventRelationLogger eventRelationLogger;
    private final CountBasedMinedProcessModel minedProcessModel;
    private final Integer refreshRate;
    private Integer receivedEvents = 0;
    private final PrecisionChecker precisionChecker;
    private final ProcessModel processModel;
    private final LossyCountingRelationCountMap<DirectlyFollowsRelation> causalEvents = new LossyCountingRelationCountMap<>();
    private final LossyCountingRelationCountMap<Gateway> parallelGateways = new LossyCountingRelationCountMap<>();
    private final LossyCountingRelationCountMap<Gateway> choiceGateways = new LossyCountingRelationCountMap<>();

    public FilterSinkLossyCounting(final Consumer<String, MinedProcessModel> consumer,
                                   final Integer refreshRate,
                                   final CountBasedMinedProcessModel minedProcessModel,
                                   final EventRelationLogger eventRelationLogger,
                                   final PrecisionChecker precisionChecker,
                                   final ProcessModel processModel) {
        super(consumer);
        this.refreshRate = refreshRate;
        this.eventRelationLogger = eventRelationLogger;
        this.precisionChecker = precisionChecker;
        this.processModel = processModel;
        this.minedProcessModel = minedProcessModel;
    }

    @Override
    public void receive(final MinedProcessModel receivingProcessModel) {
        final int currentBucketId = (int) Math.ceil((double) receivedEvents / refreshRate);

        if (receivingProcessModel == null) {
            return;
        }

        updateStoredEventFromReceivingEvent(receivingProcessModel.getCausalEvents(), causalEvents, currentBucketId);
        updateStoredEventFromReceivingEvent(receivingProcessModel.getParallelGateways(), parallelGateways, currentBucketId);
        updateStoredEventFromReceivingEvent(receivingProcessModel.getChoiceGateways(), choiceGateways, currentBucketId);

        if (receivedEvents % refreshRate == 0) {
            updateModelFromCollectedPattern(causalEvents, minedProcessModel.getCausalEventMap(), currentBucketId);
            updateModelFromCollectedPattern(parallelGateways, minedProcessModel.getAndGateways(), currentBucketId);
            updateModelFromCollectedPattern(choiceGateways, minedProcessModel.getXorGateways(), currentBucketId);

            eventRelationLogger.logRelations(minedProcessModel);
            precisionChecker.calculatePrecision(processModel, minedProcessModel);
        }

        receivedEvents++;
    }

    private <T> void updateModelFromCollectedPattern(final LossyCountingRelationCountMap<T> storedRelation, final MicroBatchRelationCountMap<T> modelRelation, final int currentBucketId) {
        storedRelation.removeIrrelevant(currentBucketId);
        storedRelation.keySet().forEach(pattern -> modelRelation.insertOrUpdate(pattern, 1));
        storedRelation.clear();
    }

    private <T> void updateStoredEventFromReceivingEvent(final Set<T> receivedRelation, final LossyCountingRelationCountMap<T> storedRelation, final int currentBucketId) {
        receivedRelation.forEach(pattern -> storedRelation.insertOrUpdate(pattern, currentBucketId - 1));
    }
}
