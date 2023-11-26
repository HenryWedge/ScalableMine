package de.cau.se.model;

import de.cau.se.datastructure.BranchPair;
import de.cau.se.datastructure.DirectlyFollows;
import de.cau.se.datastructure.Gateway;
import de.cau.se.map.ResultMap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class ModelUpdater {
    private final Double andThreshold;
    private final Double dependencyThreshold;
    private final MinedProcessModel processModel;

    public ModelUpdater(final Double andThreshold,
                        final Double dependencyThreshold,
                        final MinedProcessModel processModel) {
        this.andThreshold = andThreshold;
        this.dependencyThreshold = dependencyThreshold;
        this.processModel = processModel;
    }


    public void findCausalEvents(final ResultMap resultMap,
                                 final DirectlyFollows newDiscoveredDirectlyFollows) {
        final Integer directlyFollowsCount = resultMap.get(newDiscoveredDirectlyFollows);
        final Integer reverseDirectlyFollowsCount = resultMap.get(newDiscoveredDirectlyFollows.getSwapped());
        if (directlyFollowsCount > ((reverseDirectlyFollowsCount * (1 + dependencyThreshold) + dependencyThreshold)) / (1 - dependencyThreshold)) {
            processModel.addCausalEvent(newDiscoveredDirectlyFollows);
        } else {
            //processModel.removeCausalEvent(newDiscoveredDirectlyFollows);
        }
    }

    public void findCausalEvents(final ResultMap resultMap,
                                 final Collection<DirectlyFollows> newDiscoveredDirectlyFollows) {
        newDiscoveredDirectlyFollows.forEach(directlyFollows -> findCausalEvents(resultMap, directlyFollows));
    }

    public void findSplits(final Set<String> frequentActivities,
                           final ResultMap resultMap) {
        mineGateway(frequentActivities,
                resultMap,
                andThreshold,
                DirectlyFollows::getPredecessor,
                DirectlyFollows::getSuccessor,
                Gateway.GatewayType.SPLIT);
    }

    public void findJoins(final Set<String> frequentActivities,
                          final ResultMap resultMap) {
        mineGateway(frequentActivities,
                resultMap,
                andThreshold,
                DirectlyFollows::getSuccessor,
                DirectlyFollows::getPredecessor,
                Gateway.GatewayType.JOIN);
    }

    private void mineGateway(final Set<String> frequentActivities,
                             final ResultMap resultMap,
                             final Double andThreshold,
                             final Function<DirectlyFollows, String> extractConnectingEvent,
                             final Function<DirectlyFollows, String> extractBranchEvent,
                             final Gateway.GatewayType gatewayType) {

        for (final String activity : frequentActivities) {
            final Set<DirectlyFollows> potentialSplits = new HashSet<>();
            for (final DirectlyFollows directlyFollows : processModel.getCausalEvents()) {
                if (activity.equals(extractConnectingEvent.apply(directlyFollows))) {
                    potentialSplits.add(directlyFollows);
                }
            }

            for (final DirectlyFollows branch1 : potentialSplits) {
                for (final DirectlyFollows branch2 : potentialSplits) {

                    final String connectingEvent = extractConnectingEvent.apply(branch1);
                    final String branchEvent1 = extractBranchEvent.apply(branch1);
                    final String branchEvent2 = extractBranchEvent.apply(branch2);

                    if (!branch1.equals(branch2) && !branchEvent1.equals(branchEvent2)) {
                        final double andMeasure = calculateAndMeasure(resultMap, branch1, branch2, branchEvent1, branchEvent2);
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

    private static double calculateAndMeasure(final ResultMap resultMap,
                                              final DirectlyFollows branch1,
                                              final DirectlyFollows branch2,
                                              final String branchEvent1,
                                              final String branchEvent2) {
        final Integer ab = resultMap.get(branch1);
        final Integer ac = resultMap.get(branch2);
        final Integer bc = resultMap.get(new DirectlyFollows(branchEvent1, branchEvent2));
        final Integer cb = resultMap.get(new DirectlyFollows(branchEvent2, branchEvent1));
        return ((double) bc + cb) / (ab + ac + 1);
    }

    public MinedProcessModel getProcessModel() {
        return processModel;
    }
}
