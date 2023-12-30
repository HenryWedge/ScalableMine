package de.cau.se.model;

import de.cau.se.datastructure.BranchPair;
import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Gateway;
import de.cau.se.map.result.RelationCountMap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * This class tracks the frequent occurring directly follows relations and updates the model according to it.
 * The dependency-measure and the and-measure are calculated in the manner of the heuristics' miner.
 */
public class ModelUpdateService {

    private final Double andThreshold;
    private final Double dependencyThreshold;
    private RelationCountMap<DirectlyFollowsRelation, ?> directlyFollowsRelationCountMap;

    public ModelUpdateService(final Double andThreshold,
                              final Double dependencyThreshold,
                              final RelationCountMap<DirectlyFollowsRelation, ?> directlyFollowsRelationCountMap) {
        this.directlyFollowsRelationCountMap = directlyFollowsRelationCountMap;
        this.andThreshold = andThreshold;
        this.dependencyThreshold = dependencyThreshold;
    }

    /**
     * Updates the model with a single directly follows relation.
     *
     * @param directlyFollowsRelation that updates the model
     */
    public void update(final MinedProcessModel processModel, final DirectlyFollowsRelation directlyFollowsRelation) {
        update(processModel, directlyFollowsRelation, 1);
    }

    public void remove(final MinedProcessModel processModel, DirectlyFollowsRelation directlyFollowsRelation) {
        processModel.getCausalEvents().remove(directlyFollowsRelation);
        processModel.getChoiceGateways().removeIf(gateway -> gateway.isBasedOnDirectlyFollowsRelation(directlyFollowsRelation));
        processModel.getParallelGateways().removeIf(gateway -> gateway.isBasedOnDirectlyFollowsRelation(directlyFollowsRelation));
    }

    /**
     * Updates the model with a directly follows relation and the count how often it occurred in a certain interval.
     *
     * @param directlyFollowsRelation      that updates the model.
     * @param directlyFollowsRelationCount how often the directly follows relation occurred in the interval.
     */
    public void update(final MinedProcessModel processModel, final DirectlyFollowsRelation directlyFollowsRelation, final Integer directlyFollowsRelationCount) {
        update(processModel,
               directlyFollowsRelation,
               directlyFollowsRelationCount,
               Set.of(directlyFollowsRelation.getSuccessor(), directlyFollowsRelation.getPredecessor()));
    }

    /**
     * Updates the model with a directly follows relation and its occurrences. Moreover, it is specified which activities
     * should be recomputed for the model.
     *
     * @param directlyFollowsRelation that updates the model.
     * @param directlyFollowsCount    how often the directly follows relation occurred in the interval.
     * @param activitiesToUpdate      activities for which new gateways are computed.
     */
    public void update(final MinedProcessModel processModel,
                       final DirectlyFollowsRelation directlyFollowsRelation,
                       final Integer directlyFollowsCount,
                       final Set<String> activitiesToUpdate) {
        directlyFollowsRelationCountMap.insertOrUpdate(directlyFollowsRelation, directlyFollowsCount);
        updateCausalEvents(processModel, directlyFollowsRelation);
        updateSplits(processModel, activitiesToUpdate);
        updateJoins(processModel, activitiesToUpdate);
    }

    /**
     * Updates the model with a directly follows relation and its occurrences. Moreover, it is specified which activities
     * should be recomputed for the model.
     *
     * @param activitiesToUpdate activities for which new gateways are computed.
     */
    public void updateProcessModel(final MinedProcessModel processModel,
                                   final RelationCountMap<DirectlyFollowsRelation, ?> directlyFollowsRelationCountMap,
                                   final Set<String> activitiesToUpdate) {
        this.directlyFollowsRelationCountMap = directlyFollowsRelationCountMap;
        updateCausalEvents(processModel);
        updateSplits(processModel, activitiesToUpdate);
        updateJoins(processModel, activitiesToUpdate);
    }

    private void updateCausalEvents(final MinedProcessModel processModel, final DirectlyFollowsRelation newDiscoveredDirectlyFollowsRelation) {
        Integer directlyFollowsCount = directlyFollowsRelationCountMap.getCountOf(newDiscoveredDirectlyFollowsRelation);
        final Integer reverseDirectlyFollowsCount = directlyFollowsRelationCountMap.getCountOf(newDiscoveredDirectlyFollowsRelation.getSwapped());
        if (directlyFollowsCount > ((reverseDirectlyFollowsCount * (1 + dependencyThreshold) + dependencyThreshold)) / (1 - dependencyThreshold)) {
            processModel.addCausalEvent(newDiscoveredDirectlyFollowsRelation);
        } else {
            processModel.removeCausalEvent(newDiscoveredDirectlyFollowsRelation);
        }
    }

    private void updateCausalEvents(final MinedProcessModel processModel) {
        for (Map.Entry<DirectlyFollowsRelation, ?> entry : directlyFollowsRelationCountMap.entrySet()) {
            updateCausalEvents(processModel, entry.getKey());
        }
    }

    public void updateSplits(final MinedProcessModel processModel, final Set<String> frequentActivities) {
        mineGateway(processModel,
                frequentActivities,
                andThreshold,
                DirectlyFollowsRelation::getPredecessor,
                DirectlyFollowsRelation::getSuccessor,
                Gateway.GatewayType.SPLIT);
    }

    public void updateJoins(final MinedProcessModel processModel, final Set<String> frequentActivities) {
        mineGateway(processModel,
                frequentActivities,
                andThreshold,
                DirectlyFollowsRelation::getSuccessor,
                DirectlyFollowsRelation::getPredecessor,
                Gateway.GatewayType.JOIN);
    }

    private void mineGateway(final MinedProcessModel processModel,
                             final Set<String> frequentActivities,
                             final Double andThreshold,
                             final Function<DirectlyFollowsRelation, String> extractConnectingActivity,
                             final Function<DirectlyFollowsRelation, String> extractBranchEvent,
                             final Gateway.GatewayType gatewayType) {

        for (final String activity : frequentActivities) {
            final Set<DirectlyFollowsRelation> potentialSplits = new HashSet<>();
            for (final DirectlyFollowsRelation directlyFollowsRelation : processModel.getCausalEvents()) {
                if (activity.equals(extractConnectingActivity.apply(directlyFollowsRelation))) {
                    potentialSplits.add(directlyFollowsRelation);
                }
            }

            for (final DirectlyFollowsRelation branch1 : potentialSplits) {
                for (final DirectlyFollowsRelation branch2 : potentialSplits) {

                    final String connectingEvent = extractConnectingActivity.apply(branch1);
                    final String branchEvent1 = extractBranchEvent.apply(branch1);
                    final String branchEvent2 = extractBranchEvent.apply(branch2);

                    if (!branch1.equals(branch2) && !branchEvent1.equals(branchEvent2)) {
                        final double andMeasure = calculateAndMeasure(branch1, branch2, branchEvent1, branchEvent2);
                        final Gateway newGateway = new Gateway(gatewayType, connectingEvent, new BranchPair(branchEvent1, branchEvent2));
                        final boolean isAndGateway = andMeasure >= andThreshold;
                        addGatewayToSet(processModel, newGateway, isAndGateway);
                    }
                }
            }
        }
    }

    private void addGatewayToSet(final MinedProcessModel processModel, final Gateway newGateway, final boolean isAndGateway) {
        if (isAndGateway) {
            processModel.addAndGateway(newGateway);
            processModel.removeXorGateway(newGateway);
        } else {
            processModel.addXorGateway(newGateway);
            processModel.removeAndGateway(newGateway);
        }
    }

    private double calculateAndMeasure(final DirectlyFollowsRelation branch1,
                                       final DirectlyFollowsRelation branch2,
                                       final String branchEvent1,
                                       final String branchEvent2) {
        final Integer ab = directlyFollowsRelationCountMap.getCountOf(branch1);
        final Integer ac = directlyFollowsRelationCountMap.getCountOf(branch2);
        final Integer bc = directlyFollowsRelationCountMap.getCountOf(new DirectlyFollowsRelation(branchEvent1, branchEvent2));
        final Integer cb = directlyFollowsRelationCountMap.getCountOf(new DirectlyFollowsRelation(branchEvent2, branchEvent1));
        return ((double) bc + cb) / (ab + ac + 1);
    }
}
