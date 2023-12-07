package de.cau.se.model;

import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Gateway;
import de.cau.se.map.result.MicroBatchRelationCountMap;
import de.cau.se.processmodel.ProcessModel;

import java.util.*;

public class CountBasedMinedProcessModel implements ProcessModel {
    private final MicroBatchRelationCountMap<DirectlyFollowsRelation> causalEvents;
    private final MicroBatchRelationCountMap<Gateway> andGateways;
    private final MicroBatchRelationCountMap<Gateway> xorGateways;

    public CountBasedMinedProcessModel() {
        this.causalEvents = new MicroBatchRelationCountMap<>();
        this.andGateways = new MicroBatchRelationCountMap<>();
        this.xorGateways = new MicroBatchRelationCountMap<>();
    }

    public MicroBatchRelationCountMap<DirectlyFollowsRelation> getCausalEventMap() {
        return causalEvents;
    }

    public MicroBatchRelationCountMap<Gateway> getAndGateways() {
        return andGateways;
    }

    public MicroBatchRelationCountMap<Gateway> getXorGateways() {
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
