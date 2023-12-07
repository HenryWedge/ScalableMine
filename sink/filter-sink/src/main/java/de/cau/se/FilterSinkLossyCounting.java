package de.cau.se;

import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Gateway;
import de.cau.se.map.result.LossyCountingRelationCountMap;
import de.cau.se.model.CountBasedMinedProcessModel;
import de.cau.se.model.EventRelationLogger;
import de.cau.se.model.MinedProcessModel;
import de.cau.se.model.PrecisionChecker;
import de.cau.se.processmodel.ProcessModel;
import org.apache.kafka.clients.consumer.Consumer;

public class FilterSinkLossyCounting extends AbstractConsumer<MinedProcessModel> {

    private final EventRelationLogger eventRelationLogger;
    private final CountBasedMinedProcessModel minedProcessModel;
    private final Integer refreshRate;
    private Integer receivedEvents = 0;
    private final PrecisionChecker precisionChecker;
    private final ProcessModel processModel;
    private final int bucketSize;
    private final LossyCountingRelationCountMap<DirectlyFollowsRelation> causalEvents = new LossyCountingRelationCountMap<>();
    private final LossyCountingRelationCountMap<Gateway> parallelGateways = new LossyCountingRelationCountMap<>();
    private final LossyCountingRelationCountMap<Gateway> choiceGateways = new LossyCountingRelationCountMap<>();

    public FilterSinkLossyCounting(final Consumer<String, MinedProcessModel> consumer,
                                   final Integer refreshRate,
                                   final Integer bucketSize,
                                   final CountBasedMinedProcessModel minedProcessModel,
                                   final EventRelationLogger eventRelationLogger,
                                   final PrecisionChecker precisionChecker,
                                   final ProcessModel processModel) {
        super(consumer);
        this.bucketSize = bucketSize;
        this.refreshRate = refreshRate;
        this.eventRelationLogger = eventRelationLogger;
        this.precisionChecker = precisionChecker;
        this.processModel = processModel;
        this.minedProcessModel = minedProcessModel;
    }

    @Override
    public void receive(final MinedProcessModel receivingProcessModel) {
        final int currentBucketId = (int) Math.ceil((double) receivedEvents / bucketSize);

        if (receivingProcessModel == null) {
            return;
        }

        receivingProcessModel.getCausalEvents().forEach(causalEvent -> causalEvents.insertOrUpdate(causalEvent, currentBucketId - 1));
        receivingProcessModel.getParallelGateways().forEach(parallelGateway -> parallelGateways.insertOrUpdate(parallelGateway, currentBucketId - 1));
        receivingProcessModel.getChoiceGateways().forEach(choiceGateway -> choiceGateways.insertOrUpdate(choiceGateway, currentBucketId - 1));

        if (receivedEvents % refreshRate == 0) {
            causalEvents.removeIrrelevant(refreshRate * currentBucketId);
            parallelGateways.removeIrrelevant(refreshRate * currentBucketId);
            choiceGateways.removeIrrelevant(refreshRate * currentBucketId);

            causalEvents.keySet().forEach(causalEvent -> minedProcessModel.getCausalEventMap().insertOrUpdate(causalEvent, 1));
            parallelGateways.keySet().forEach(parallelGateway -> minedProcessModel.getAndGateways().insertOrUpdate(parallelGateway, 1));
            choiceGateways.keySet().forEach(choiceGateway -> minedProcessModel.getXorGateways().insertOrUpdate(choiceGateway, 1));

            causalEvents.clear();
            parallelGateways.clear();
            choiceGateways.clear();

            eventRelationLogger.logRelations(minedProcessModel);
            precisionChecker.calculatePrecision(processModel, minedProcessModel);
        }

        receivedEvents++;
    }

}
