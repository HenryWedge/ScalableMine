package de.cau.se.processmodel;

import de.cau.se.datastructure.BranchPair;
import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Gateway;

import java.util.Set;

public class SmallProcessModel implements ProcessModel {

    @Override
    public Set<DirectlyFollowsRelation> getCausalEvents() {
        return Set.of(
                new DirectlyFollowsRelation("A", "C"),
                new DirectlyFollowsRelation("A", "E"),
                new DirectlyFollowsRelation("E", "F"),//
                new DirectlyFollowsRelation("C", "D"),//
                new DirectlyFollowsRelation("D", "B"),
                new DirectlyFollowsRelation("B", "G"),
                new DirectlyFollowsRelation("G", "I"),
                new DirectlyFollowsRelation("G", "J"),
                new DirectlyFollowsRelation("I", "H"),
                new DirectlyFollowsRelation("H", "K"),
                new DirectlyFollowsRelation("K", "M"),
                new DirectlyFollowsRelation("K", "N"),
                new DirectlyFollowsRelation("K", "O"),
                new DirectlyFollowsRelation("K", "P"),
                new DirectlyFollowsRelation("M", "L"),
                new DirectlyFollowsRelation("N", "L"),
                new DirectlyFollowsRelation("O", "L"),
                new DirectlyFollowsRelation("P", "L"));
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
