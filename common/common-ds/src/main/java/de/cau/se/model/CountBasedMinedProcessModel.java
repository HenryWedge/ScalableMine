package de.cau.se.model;

import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Gateway;
import de.cau.se.map.result.CountBasedRelationCountMap;
import de.cau.se.processmodel.ProcessModel;

import java.util.*;

/**
 * Class that stores the behavioral patterns of a process model with their number of occurrences.
 */
public class CountBasedMinedProcessModel implements ProcessModel {
    private final CountBasedRelationCountMap<DirectlyFollowsRelation> causalEvents;
    private final CountBasedRelationCountMap<Gateway> andGateways;
    private final CountBasedRelationCountMap<Gateway> xorGateways;

    public CountBasedMinedProcessModel() {
        this.causalEvents = new CountBasedRelationCountMap<>();
        this.andGateways = new CountBasedRelationCountMap<>();
        this.xorGateways = new CountBasedRelationCountMap<>();
    }

    public CountBasedRelationCountMap<DirectlyFollowsRelation> getCausalEventMap() {
        return causalEvents;
    }

    public CountBasedRelationCountMap<Gateway> getAndGateways() {
        return andGateways;
    }

    public CountBasedRelationCountMap<Gateway> getXorGateways() {
        return xorGateways;
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
