package de.cau.se.processmodel;

import de.cau.se.datastructure.BranchPair;
import de.cau.se.datastructure.DirectlyFollows;
import de.cau.se.datastructure.Gateway;

import java.util.Set;

public class SmallProcessModel implements ProcessModel {

    @Override
    public Set<DirectlyFollows> getCausalEvents() {
        return Set.of(
                new DirectlyFollows("A", "C"),
                new DirectlyFollows("A", "E"),
                new DirectlyFollows("E", "F"),//
                new DirectlyFollows("C", "D"),//
                new DirectlyFollows("D", "B"),
                new DirectlyFollows("B", "G"),
                new DirectlyFollows("G", "I"),
                new DirectlyFollows("G", "J"),
                new DirectlyFollows("I", "H"),
                new DirectlyFollows("H", "K"),
                new DirectlyFollows("K", "M"),
                new DirectlyFollows("K", "N"),
                new DirectlyFollows("K", "O"),
                new DirectlyFollows("K", "P"),
                new DirectlyFollows("M", "L"),
                new DirectlyFollows("N", "L"),
                new DirectlyFollows("O", "L"),
                new DirectlyFollows("P", "L"));
        // JH, ED, FB, CF
    }

    @Override
    public Set<Gateway> getParallelGateways() {
        return Set.of(
                new Gateway(Gateway.GatewayType.SPLIT, "A", new BranchPair("E", "C")),
                new Gateway(Gateway.GatewayType.JOIN, "B", new BranchPair("F", "D")),
                new Gateway(Gateway.GatewayType.SPLIT, "K", new BranchPair("M", "N")),
                new Gateway(Gateway.GatewayType.SPLIT, "K", new BranchPair("M", "O")),
                new Gateway(Gateway.GatewayType.SPLIT, "K", new BranchPair("M", "P")),
                new Gateway(Gateway.GatewayType.SPLIT, "K", new BranchPair("N", "O")),
                new Gateway(Gateway.GatewayType.SPLIT, "K", new BranchPair("N", "P")),
                new Gateway(Gateway.GatewayType.SPLIT, "K", new BranchPair("O", "P")),
                new Gateway(Gateway.GatewayType.JOIN, "L", new BranchPair("M", "N")),
                new Gateway(Gateway.GatewayType.JOIN, "L", new BranchPair("M", "O")),
                new Gateway(Gateway.GatewayType.JOIN, "L", new BranchPair("M", "P")),
                new Gateway(Gateway.GatewayType.JOIN, "L", new BranchPair("N", "O")),
                new Gateway(Gateway.GatewayType.JOIN, "L", new BranchPair("N", "P")),
                new Gateway(Gateway.GatewayType.JOIN, "L", new BranchPair("O", "P")));
    }

    @Override
    public Set<Gateway> getChoiceGateways() {
        return Set.of(new Gateway(Gateway.GatewayType.SPLIT, "G", new BranchPair("I", "J")),
                new Gateway(Gateway.GatewayType.JOIN, "G", new BranchPair("I", "J")));
    }
}
