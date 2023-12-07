package de.cau.se.model;

import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Gateway;
import de.cau.se.processmodel.ProcessModel;

import java.util.*;

public class CountBasedMinedProcessModel implements ProcessModel {

    private final Map<DirectlyFollowsRelation, Integer> causalEvents;
    private final Map<Gateway, Integer> andGateways;
    private final Map<Gateway, Integer> xorGateways;

    public CountBasedMinedProcessModel() {
        this.causalEvents = new HashMap<>();
        this.andGateways = new HashMap<>();
        this.xorGateways = new HashMap<>();
    }


    public void addCausalEvent(final DirectlyFollowsRelation directlyFollowsRelation) {
        if (causalEvents.containsKey(directlyFollowsRelation)) {
            causalEvents.put(directlyFollowsRelation, causalEvents.get(directlyFollowsRelation) + 1);
        } else {
            causalEvents.put(directlyFollowsRelation, 1);
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
        removeIrrelevant(causalEvents, relevanceThreshold);
        removeIrrelevant(andGateways, relevanceThreshold);
        removeIrrelevant(xorGateways, relevanceThreshold);
    }

    private void removeIrrelevant(final Map<?, Integer> countMap, final int relevanceThreshold) {
        countMap.entrySet().removeIf(entry -> entry.getValue() >= relevanceThreshold);
    }

    @Override
    public Set<DirectlyFollowsRelation> getCausalEvents() {
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
