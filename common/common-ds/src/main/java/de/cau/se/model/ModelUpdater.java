package de.cau.se.model;

import de.cau.se.datastructure.BranchPair;
import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Gateway;
import de.cau.se.map.result.IResultMap;


import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * This class tracks the frequent occurring directly follows relations and updates the model according to it.
 * The dependency-measure and the and-measure are calculated in the manner of the heuristics' miner.
 */
public class ModelUpdater {

    private final Double andThreshold;
    private final Double dependencyThreshold;
    private final MinedProcessModel processModel;
    private IResultMap<DirectlyFollowsRelation, ?> directlyFollowsRelationCountMap;

    public ModelUpdater(final Double andThreshold,
                        final Double dependencyThreshold,
                        final MinedProcessModel processModel,
                        final IResultMap<DirectlyFollowsRelation, ?> directlyFollowsRelationCountMap) {
        this.directlyFollowsRelationCountMap = directlyFollowsRelationCountMap;
        this.andThreshold = andThreshold;
        this.dependencyThreshold = dependencyThreshold;
        this.processModel = processModel;
    }

    /**
     * Updates the model with a single directly follows relation.
     *
     * @param directlyFollowsRelation that updates the model
     */
    public void update(final DirectlyFollowsRelation directlyFollowsRelation) {
        update(directlyFollowsRelation, 1);
    }

    public void remove(DirectlyFollowsRelation directlyFollowsRelation) {
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
    public void update(final DirectlyFollowsRelation directlyFollowsRelation, final Integer directlyFollowsRelationCount) {
        update(directlyFollowsRelation,
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
    public void update(final DirectlyFollowsRelation directlyFollowsRelation,
                       final Integer directlyFollowsCount,
                       final Set<String> activitiesToUpdate) {
        directlyFollowsRelationCountMap.insertOrUpdate(directlyFollowsRelation, directlyFollowsCount);
        findCausalEvents(directlyFollowsRelation);
        findSplits(activitiesToUpdate);
        findJoins(activitiesToUpdate);
    }

    /**
     * Updates the model with a directly follows relation and its occurrences. Moreover, it is specified which activities
     * should be recomputed for the model.
     *
     * @param activitiesToUpdate activities for which new gateways are computed.
     */
    public void update(final IResultMap<DirectlyFollowsRelation, ?> directlyFollowsRelationCountMap,
                       final Set<String> activitiesToUpdate) {
        this.directlyFollowsRelationCountMap = directlyFollowsRelationCountMap;
        findCausalEvents();
        findSplits(activitiesToUpdate);
        findJoins(activitiesToUpdate);
    }

    private void findCausalEvents(final DirectlyFollowsRelation newDiscoveredDirectlyFollowsRelation) {
        Integer directlyFollowsCount = directlyFollowsRelationCountMap.getCountOf(newDiscoveredDirectlyFollowsRelation);
        final Integer reverseDirectlyFollowsCount = directlyFollowsRelationCountMap.getCountOf(newDiscoveredDirectlyFollowsRelation.getSwapped());
        if (directlyFollowsCount > ((reverseDirectlyFollowsCount * (1 + dependencyThreshold) + dependencyThreshold)) / (1 - dependencyThreshold)) {
            processModel.addCausalEvent(newDiscoveredDirectlyFollowsRelation);
        } else {
            processModel.removeCausalEvent(newDiscoveredDirectlyFollowsRelation);
        }
    }

    private void findCausalEvents() {
        for (Map.Entry<DirectlyFollowsRelation, ?> entry : directlyFollowsRelationCountMap.entrySet()) {
            findCausalEvents(entry.getKey());
        }
    }

    public void findSplits(final Set<String> frequentActivities) {
        mineGateway(frequentActivities,
                andThreshold,
                DirectlyFollowsRelation::getPredecessor,
                DirectlyFollowsRelation::getSuccessor,
                Gateway.GatewayType.SPLIT);
    }

    public void findJoins(final Set<String> frequentActivities) {
        mineGateway(frequentActivities,
                andThreshold,
                DirectlyFollowsRelation::getSuccessor,
                DirectlyFollowsRelation::getPredecessor,
                Gateway.GatewayType.JOIN);
    }

    private void mineGateway(final Set<String> frequentActivities,
                             final Double andThreshold,
                             final Function<DirectlyFollowsRelation, String> extractConnectingEvent,
                             final Function<DirectlyFollowsRelation, String> extractBranchEvent,
                             final Gateway.GatewayType gatewayType) {

        for (final String activity : frequentActivities) {
            final Set<DirectlyFollowsRelation> potentialSplits = new HashSet<>();
            for (final DirectlyFollowsRelation directlyFollowsRelation : processModel.getCausalEvents()) {
                if (activity.equals(extractConnectingEvent.apply(directlyFollowsRelation))) {
                    potentialSplits.add(directlyFollowsRelation);
                }
            }

            for (final DirectlyFollowsRelation branch1 : potentialSplits) {
                for (final DirectlyFollowsRelation branch2 : potentialSplits) {

                    final String connectingEvent = extractConnectingEvent.apply(branch1);
                    final String branchEvent1 = extractBranchEvent.apply(branch1);
                    final String branchEvent2 = extractBranchEvent.apply(branch2);

                    if (!branch1.equals(branch2) && !branchEvent1.equals(branchEvent2)) {
                        final double andMeasure = calculateAndMeasure(branch1, branch2, branchEvent1, branchEvent2);
                        final Gateway newGateway = new Gateway(gatewayType, connectingEvent, new BranchPair(branchEvent1, branchEvent2));
                        final boolean isAndGateway = andMeasure >= andThreshold;
                        addGatewayToSet(newGateway, isAndGateway);
                    }
                }
            }
        }
    }

    private void addGatewayToSet(final Gateway newGateway, final boolean isAndGateway) {
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

    public MinedProcessModel getProcessModel() {
        return processModel;
    }
}
