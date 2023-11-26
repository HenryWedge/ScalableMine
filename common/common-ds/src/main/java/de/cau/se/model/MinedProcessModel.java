package de.cau.se.model;

import de.cau.se.datastructure.DirectlyFollows;
import de.cau.se.datastructure.Gateway;
import de.cau.se.processmodel.ProcessModel;

import java.util.HashSet;
import java.util.Set;

public class MinedProcessModel implements ProcessModel {
    private Set<DirectlyFollows> causalEvents;
    private Set<Gateway> andGateways;
    private Set<Gateway> xorGateways;

    public MinedProcessModel() {
        this.causalEvents = new HashSet<>();
        this.andGateways = new HashSet<>();
        this.xorGateways = new HashSet<>();
    }


    public void addCausalEvent(final DirectlyFollows directlyFollows) {
        causalEvents.add(directlyFollows);
    }

    public void addAndGateway(final Gateway andGateway) {
        andGateways.add(andGateway);
    }

    public void addXorGateway(final Gateway xorGateway) {
        xorGateways.add(xorGateway);
    }

    public void removeCausalEvent(final DirectlyFollows directlyFollows) {
        causalEvents.remove(directlyFollows);
    }

    public void removeAndGateway(final Gateway andGateway) {
        andGateways.remove(andGateway);
    }

    public void removeXorGateway(final Gateway xorGateway) {
        xorGateways.remove(xorGateway);
    }


    @Override
    public Set<DirectlyFollows> getCausalEvents() {
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

    public void setCausalEvents(Set<DirectlyFollows> causalEvents) {
        this.causalEvents = causalEvents;
    }

    public void setAndGateways(Set<Gateway> andGateways) {
        this.andGateways = andGateways;
    }

    public void setXorGateways(Set<Gateway> xorGateways) {
        this.xorGateways = xorGateways;
    }
}
