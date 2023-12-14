package de.cau.se.model;

import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Gateway;
import de.cau.se.processmodel.ProcessModel;

import java.util.HashSet;
import java.util.Set;

public class MinedProcessModel implements ProcessModel {
    private Set<DirectlyFollowsRelation> causalEvents;
    private Set<Gateway> andGateways;
    private Set<Gateway> xorGateways;

    public MinedProcessModel() {
        this.causalEvents = new HashSet<>();
        this.andGateways = new HashSet<>();
        this.xorGateways = new HashSet<>();
    }


    public void addCausalEvent(final DirectlyFollowsRelation directlyFollowsRelation) {
        causalEvents.add(directlyFollowsRelation);
    }

    public void addAndGateway(final Gateway andGateway) {
        andGateways.add(andGateway);
    }

    public void addXorGateway(final Gateway xorGateway) {
        xorGateways.add(xorGateway);
    }

    public void removeCausalEvent(final DirectlyFollowsRelation directlyFollowsRelation) {
        causalEvents.remove(directlyFollowsRelation);
    }

    public void removeAndGateway(final Gateway andGateway) {
        andGateways.remove(andGateway);
    }

    public void removeXorGateway(final Gateway xorGateway) {
        xorGateways.remove(xorGateway);
    }

    public void setCausalEvents(Set<DirectlyFollowsRelation> causalEvents) {
        this.causalEvents = causalEvents;
    }

    public Set<Gateway> getAndGateways() {
        return andGateways;
    }

    public void setAndGateways(Set<Gateway> andGateways) {
        this.andGateways = andGateways;
    }

    public Set<Gateway> getXorGateways() {
        return xorGateways;
    }

    public void setXorGateways(Set<Gateway> xorGateways) {
        this.xorGateways = xorGateways;
    }

    @Override
    public Set<DirectlyFollowsRelation> getCausalEvents() {
        return causalEvents;
    }

    @Override
    public Set<Gateway> getParallelGateways() {
        return andGateways;
    }

    @Override
    public Set<Gateway> getChoiceGateways() {
        return xorGateways;
    }
}
