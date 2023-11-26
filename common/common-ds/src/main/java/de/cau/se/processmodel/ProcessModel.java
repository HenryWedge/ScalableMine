package de.cau.se.processmodel;

import de.cau.se.datastructure.DirectlyFollows;
import de.cau.se.datastructure.Event;
import de.cau.se.datastructure.Gateway;

import java.util.Set;

public interface ProcessModel {

    Set<DirectlyFollows> getCausalEvents();
    Set<Gateway> getParallelGateways();
    Set<Gateway> getChoiceGateways();
}
