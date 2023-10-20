package de.cau.se.model;

import de.cau.se.datastructure.BranchPair;
import de.cau.se.datastructure.DirectlyFollows;
import de.cau.se.datastructure.Gateway;
import de.cau.se.map.ResultMap;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class ModelUpdater {

    public void findCausalEvents(final Set<DirectlyFollows> causalEvents,
                                 final ResultMap resultMap,
                                 final Double dependencyThreshold,
                                 final DirectlyFollows newDiscoveredDirectlyFollows) {
        if (causalEvents.contains(newDiscoveredDirectlyFollows)) {
            return;
        }
        Integer directlyFollowsCount = resultMap.get(newDiscoveredDirectlyFollows);
        Integer reverseDirectlyFollowsCount = resultMap.get(newDiscoveredDirectlyFollows.getSwapped());
        if (directlyFollowsCount > ((reverseDirectlyFollowsCount * (1 + dependencyThreshold) + dependencyThreshold)) / (1 - dependencyThreshold)) {
            causalEvents.add(newDiscoveredDirectlyFollows);
        }
    }

    public void findSplits(final Set<String> frequentActivities,
                           final Set<DirectlyFollows> causalEvents,
                           final ResultMap resultMap,
                           final Double andThreshold,
                           final Set<Gateway> andSplits,
                           final Set<Gateway> xorSplits) {
        findGateway(frequentActivities,
                causalEvents,
                resultMap,
                andThreshold,
                andSplits,
                xorSplits,
                DirectlyFollows::getPredecessor,
                DirectlyFollows::getSuccessor,
                Gateway.GatewayType.SPLIT);
    }

    public void findJoins(final Set<String> frequentActivities,
                          final Set<DirectlyFollows> causalEvents,
                          final ResultMap resultMap,
                          final Double andThreshold,
                          final Set<Gateway> andJoins,
                          final Set<Gateway> xorJoins) {
        findGateway(frequentActivities,
                causalEvents,
                resultMap,
                andThreshold,
                andJoins,
                xorJoins,
                DirectlyFollows::getSuccessor,
                DirectlyFollows::getPredecessor,
                Gateway.GatewayType.JOIN);
    }

    private void findGateway(final Set<String> frequentActivities,
                             final Set<DirectlyFollows> causalEvents,
                             final ResultMap resultMap,
                             final Double andThreshold,
                             final Set<Gateway> andGateways,
                             final Set<Gateway> xorGateways,
                             final Function<DirectlyFollows, String> extractConnectingEvent,
                             final Function<DirectlyFollows, String> extractBranchEvent,
                             final Gateway.GatewayType gatewayType) {

        for (final String activity : frequentActivities) {
            final Set<DirectlyFollows> potentialSplits = new HashSet<>();
            for (final DirectlyFollows directlyFollows : causalEvents) {
                if (activity.equals(extractConnectingEvent.apply(directlyFollows))) {
                    potentialSplits.add(directlyFollows);
                }
            }

            for (final DirectlyFollows branch1 : potentialSplits) {
                for (final DirectlyFollows branch2 : potentialSplits) {

                    String connectingEvent = extractConnectingEvent.apply(branch1);
                    String branchEvent1 = extractBranchEvent.apply(branch1);
                    String branchEvent2 = extractBranchEvent.apply(branch2);

                    if (!branch1.equals(branch2) && !branchEvent1.equals(branchEvent2)) {
                        final double andMeasure = calculateAndMeasure(resultMap, branch1, branch2, branchEvent1, branchEvent2);
                        final Gateway newGateway = new Gateway(gatewayType, connectingEvent, new BranchPair(branchEvent1, branchEvent2));
                        final boolean isAndGateway = andMeasure >= andThreshold;
                        addGatewayToSet(newGateway, isAndGateway ? andGateways : xorGateways, isAndGateway ? xorGateways : andGateways);
                    }
                }
            }
        }
    }

    private static void addGatewayToSet(final Gateway newGateway, final Set<Gateway> setToAdd, final Set<Gateway> setToRemove) {
        setToAdd.add(newGateway);
        setToRemove.remove(newGateway);
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


}
