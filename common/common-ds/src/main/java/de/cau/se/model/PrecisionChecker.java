package de.cau.se.model;

import de.cau.se.datastructure.BranchPair;
import de.cau.se.datastructure.DirectlyFollows;
import de.cau.se.datastructure.Gateway;

import java.util.Set;

public class PrecisionChecker {

    public void calculatePrecision(final Set<DirectlyFollows> causalEvents,
                                   final Set<Gateway> parallelEvents,
                                   final Set<Gateway> xorEvents) {
        Set<DirectlyFollows> expectedCausalEvents = Set.of(
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
                new DirectlyFollows("P", "L")
                // JH, ED, FB, CF
        );
        Set<Gateway> expectedParallelEvents = Set.of(
                new Gateway(Gateway.GatewayType.SPLIT, "A",new BranchPair("E", "C")),
                new Gateway(Gateway.GatewayType.JOIN, "B", new BranchPair("F", "D")),
                new Gateway(Gateway.GatewayType.SPLIT, "K",new BranchPair("M", "N")),
                new Gateway(Gateway.GatewayType.SPLIT, "K",new BranchPair("M", "O")),
                new Gateway(Gateway.GatewayType.SPLIT, "K",new BranchPair("M", "P")),
                new Gateway(Gateway.GatewayType.SPLIT, "K",new BranchPair("N", "O")),
                new Gateway(Gateway.GatewayType.SPLIT, "K",new BranchPair("N", "P")),
                new Gateway(Gateway.GatewayType.SPLIT, "K",new BranchPair("O", "P")),
                new Gateway(Gateway.GatewayType.JOIN, "L", new BranchPair("M", "N")),
                new Gateway(Gateway.GatewayType.JOIN, "L", new BranchPair("M", "O")),
                new Gateway(Gateway.GatewayType.JOIN, "L", new BranchPair("M", "P")),
                new Gateway(Gateway.GatewayType.JOIN, "L", new BranchPair("N", "O")),
                new Gateway(Gateway.GatewayType.JOIN, "L", new BranchPair("N", "P")),
                new Gateway(Gateway.GatewayType.JOIN, "L", new BranchPair("O", "P"))
        );

        Set<Gateway> expectedXorEvents =
                Set.of(new Gateway(Gateway.GatewayType.SPLIT, "G", new BranchPair("I", "J")),
                        new Gateway(Gateway.GatewayType.JOIN, "G", new BranchPair("I", "J")));


        long causalMatches = countMatches(causalEvents, expectedCausalEvents);
        long parallelMatches = countGatewayMatches(parallelEvents, expectedParallelEvents);
        long xorMatches = countGatewayMatches(xorEvents, expectedXorEvents);

        final double precision = (double) (causalMatches + parallelMatches + xorMatches) /
                (expectedCausalEvents.size() + expectedParallelEvents.size() + expectedXorEvents.size());
        System.out.printf("Precision: %.2f \n", precision);
    }

    private long countMatches(final Set<DirectlyFollows> actual, final Set<DirectlyFollows> expected) {
        return actual.stream().filter(expected::contains).count();
    }

    private long countGatewayMatches(final Set<Gateway> actual, final Set<Gateway> expected) {
        return actual.stream().filter(expected::contains).count();
    }

}
