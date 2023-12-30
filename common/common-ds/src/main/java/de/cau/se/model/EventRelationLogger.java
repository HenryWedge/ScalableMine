package de.cau.se.model;


import de.cau.se.processmodel.ProcessModel;

/**
 * Logs a process model
 */
public class EventRelationLogger {

    public void logRelations(final ProcessModel processModel) {
        System.out.println("-----------------------------------------");
        System.out.println("Causal events: \n");
        processModel.getCausalEvents().forEach(causalRelation -> System.out.println(causalRelation.toString()));
        System.out.println("Parallel Gateways: \n");
        processModel.getParallelGateways().forEach(gateway -> System.out.println(gateway.toString()));
        System.out.println("Xor Gateways: \n");
        processModel.getChoiceGateways().forEach(gateway -> System.out.println(gateway.toString()));
    }
}
