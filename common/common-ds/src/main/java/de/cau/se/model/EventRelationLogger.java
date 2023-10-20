package de.cau.se.model;


import de.cau.se.datastructure.DirectlyFollows;
import de.cau.se.datastructure.Gateway;

import java.util.Set;

public class EventRelationLogger {

    public void logRelations(final Set<DirectlyFollows> causalEvents, final Set<Gateway> parallelEvents, final Set<Gateway> xorEvents) {
        System.out.println("-----------------------------------------");
        System.out.println("Causal events: \n");
        causalEvents.forEach(causalRelation -> System.out.println(causalRelation.toString()));
        System.out.println("Parallel Gateways: \n");
        parallelEvents.forEach(gateway -> System.out.println(gateway.toString()));
        System.out.println("Xor Gateways: \n");
        xorEvents.forEach(gateway -> System.out.println(gateway.toString()));
    }
}
