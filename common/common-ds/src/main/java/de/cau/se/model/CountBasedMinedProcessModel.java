package de.cau.se.model;

import de.cau.se.datastructure.DirectlyFollows;
import de.cau.se.datastructure.Gateway;
import de.cau.se.processmodel.ProcessModel;

import java.util.*;
import java.util.stream.Collectors;

public class CountBasedMinedProcessModel implements ProcessModel {

    private Map<DirectlyFollows, Integer> causalEvents;
    private Map<Gateway, Integer> andGateways;
    private Map<Gateway, Integer> xorGateways;

    public CountBasedMinedProcessModel() {
        this.causalEvents = new HashMap<>();
        this.andGateways = new HashMap<>();
        this.xorGateways = new HashMap<>();
    }


    public void addCausalEvent(final DirectlyFollows directlyFollows) {
        if (causalEvents.containsKey(directlyFollows)) {
            causalEvents.put(directlyFollows, causalEvents.get(directlyFollows) + 1);
        } else {
            causalEvents.put(directlyFollows, 1);
        }
    }

    public void addAndGateway(final Gateway andGateway) {
        if (andGateways.containsKey(andGateway)) {
            andGateways.put(andGateway, andGateways.get(andGateway) + 1);
        } else {
            andGateways.put(andGateway, 1);
        }
    }

    public void addXorGateway(final Gateway xorGateway) {
        if (xorGateways.containsKey(xorGateway)) {
            xorGateways.put(xorGateway, xorGateways.get(xorGateway) + 1);
        } else {
            xorGateways.put(xorGateway, 1);
        }
    }

    public void removeGatewaysAndDirectlyFollowsRelationsBelowRelevanceThreshold(final int relevanceThreshold) {
        causalEvents = removeIrrelevant(causalEvents, relevanceThreshold);
        andGateways = removeIrrelevant(andGateways, relevanceThreshold);
        xorGateways = removeIrrelevant(xorGateways, relevanceThreshold);
    }

    private <T> Map<T, Integer> removeIrrelevant(final Map<T, Integer> countMap, final int relevanceThreshold) {
        return countMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue() >= relevanceThreshold)
                .collect(Collectors.toMap(Map.Entry::getKey, a -> 0));
    }

    @Override
    public Set<DirectlyFollows> getCausalEvents() {
        return causalEvents.keySet();
    }

    @Override
    public Set<Gateway> getParallelGateways() {
        return andGateways.keySet();
    }


    @Override
    public Set<Gateway> getChoiceGateways() {
        return xorGateways.keySet();
    }
}
