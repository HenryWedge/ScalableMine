package de.cau.se.processmodel;

import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Gateway;

import java.util.Set;

public interface ProcessModel {

    Set<DirectlyFollowsRelation> getCausalEvents();
    Set<Gateway> getParallelGateways();
    Set<Gateway> getChoiceGateways();
}
