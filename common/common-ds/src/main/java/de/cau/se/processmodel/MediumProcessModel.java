package de.cau.se.processmodel;

import de.cau.se.datastructure.BranchPair;
import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Gateway;

import java.util.Set;

public class MediumProcessModel implements ProcessModel {

    @Override
    public Set<DirectlyFollowsRelation> getCausalEvents() {
        return Set.of(
                new DirectlyFollowsRelation("A", "C"),
                new DirectlyFollowsRelation("A", "D"),
                new DirectlyFollowsRelation("C", "B"),
                new DirectlyFollowsRelation("D", "B"),
                new DirectlyFollowsRelation("B", "E"),
                new DirectlyFollowsRelation("E", "H"),
                new DirectlyFollowsRelation("E", "I"),
                new DirectlyFollowsRelation("E", "G"),
                new DirectlyFollowsRelation("H", "F"),
                new DirectlyFollowsRelation("I", "F"),
                new DirectlyFollowsRelation("G", "F"),
                new DirectlyFollowsRelation("F", "J"),
                new DirectlyFollowsRelation("J", "K"),
                new DirectlyFollowsRelation("K", "L"),
                new DirectlyFollowsRelation("L", "N"),
                new DirectlyFollowsRelation("L", "O"),
                new DirectlyFollowsRelation("L", "P"),
                new DirectlyFollowsRelation("N", "M"),
                new DirectlyFollowsRelation("O", "M"),
                new DirectlyFollowsRelation("P", "M"),
                new DirectlyFollowsRelation("M", "Q"),
                new DirectlyFollowsRelation("Q", "AP"),
                new DirectlyFollowsRelation("Q", "S"),
                new DirectlyFollowsRelation("AP", "AR"),
                new DirectlyFollowsRelation("AP", "AS"),
                new DirectlyFollowsRelation("AP", "AT"),
                new DirectlyFollowsRelation("AR", "AQ"),
                new DirectlyFollowsRelation("AS", "AQ"),
                new DirectlyFollowsRelation("AT", "AQ"),
                new DirectlyFollowsRelation("AQ", "AU"),
                new DirectlyFollowsRelation("AU", "AV"),
                new DirectlyFollowsRelation("AV", "R"),
                new DirectlyFollowsRelation("S", "U"),
                new DirectlyFollowsRelation("S", "Z"),
                new DirectlyFollowsRelation("S", "AE"),
                new DirectlyFollowsRelation("S", "AK"),
                new DirectlyFollowsRelation("U", "W"),
                new DirectlyFollowsRelation("U", "X"),
                new DirectlyFollowsRelation("U", "Y"),
                new DirectlyFollowsRelation("W", "V"),
                new DirectlyFollowsRelation("X", "V"),
                new DirectlyFollowsRelation("Y", "V"),
                new DirectlyFollowsRelation("V", "T"),
                new DirectlyFollowsRelation("T", "R"),
                new DirectlyFollowsRelation("Z", "AB"),
                new DirectlyFollowsRelation("Z", "AC"),
                new DirectlyFollowsRelation("Z", "AD"),
                new DirectlyFollowsRelation("AB", "AA"),
                new DirectlyFollowsRelation("AC", "AA"),
                new DirectlyFollowsRelation("AD", "AA"),
                new DirectlyFollowsRelation("AA", "T"),
                new DirectlyFollowsRelation("AE", "AG"),
                new DirectlyFollowsRelation("AE", "AH"),
                new DirectlyFollowsRelation("AE", "AI"),
                new DirectlyFollowsRelation("AE", "AJ"),
                new DirectlyFollowsRelation("AG", "AF"),
                new DirectlyFollowsRelation("AH", "AF"),
                new DirectlyFollowsRelation("AI", "AF"),
                new DirectlyFollowsRelation("AJ", "AF"),
                new DirectlyFollowsRelation("AF", "T"),
                new DirectlyFollowsRelation("AK", "AM"),
                new DirectlyFollowsRelation("AK", "AN"),
                new DirectlyFollowsRelation("AK", "AO"),
                new DirectlyFollowsRelation("AM", "AL"),
                new DirectlyFollowsRelation("AN", "AL"),
                new DirectlyFollowsRelation("AO", "AL"),
                new DirectlyFollowsRelation("AL", "T"));
    }

    @Override
    public Set<Gateway> getParallelGateways() {
        return Set.of(
                new Gateway(Gateway.GatewayType.SPLIT, "L", new BranchPair("N", "O")),
                new Gateway(Gateway.GatewayType.SPLIT, "L", new BranchPair("N", "P")),
                new Gateway(Gateway.GatewayType.SPLIT, "L", new BranchPair("P", "O")),
                new Gateway(Gateway.GatewayType.JOIN, "M", new BranchPair("N", "O")),
                new Gateway(Gateway.GatewayType.JOIN, "M", new BranchPair("N", "P")),
                new Gateway(Gateway.GatewayType.JOIN, "M", new BranchPair("P", "O")),

                new Gateway(Gateway.GatewayType.SPLIT, "AP", new BranchPair("AR", "AS")),
                new Gateway(Gateway.GatewayType.SPLIT, "AP", new BranchPair("AR", "AT")),
                new Gateway(Gateway.GatewayType.SPLIT, "AP", new BranchPair("AS", "AT")),
                new Gateway(Gateway.GatewayType.JOIN, "AQ", new BranchPair("AR", "AS")),
                new Gateway(Gateway.GatewayType.JOIN, "AQ", new BranchPair("AR", "AT")),
                new Gateway(Gateway.GatewayType.JOIN, "AQ", new BranchPair("AS", "AT")),

                new Gateway(Gateway.GatewayType.SPLIT, "U", new BranchPair("W", "X")),
                new Gateway(Gateway.GatewayType.SPLIT, "U", new BranchPair("W", "Y")),
                new Gateway(Gateway.GatewayType.SPLIT, "U", new BranchPair("X", "Y")),
                new Gateway(Gateway.GatewayType.JOIN, "V", new BranchPair("W", "X")),
                new Gateway(Gateway.GatewayType.JOIN, "V", new BranchPair("W", "Y")),
                new Gateway(Gateway.GatewayType.JOIN, "V", new BranchPair("X", "Y")),

                new Gateway(Gateway.GatewayType.SPLIT, "Z", new BranchPair("AB", "AC")),
                new Gateway(Gateway.GatewayType.SPLIT, "Z", new BranchPair("AB", "AD")),
                new Gateway(Gateway.GatewayType.SPLIT, "Z", new BranchPair("AC", "AD")),
                new Gateway(Gateway.GatewayType.JOIN, "AA", new BranchPair("AB", "AC")),
                new Gateway(Gateway.GatewayType.JOIN, "AA", new BranchPair("AB", "AD")),
                new Gateway(Gateway.GatewayType.JOIN, "AA", new BranchPair("AC", "AD"))
        );
    }

    @Override
    public Set<Gateway> getChoiceGateways() {
        return Set.of(
                new Gateway(Gateway.GatewayType.SPLIT, "A", new BranchPair("C", "D")),
                new Gateway(Gateway.GatewayType.JOIN, "B", new BranchPair("C", "D")),

                new Gateway(Gateway.GatewayType.SPLIT, "E", new BranchPair("H", "I")),
                new Gateway(Gateway.GatewayType.SPLIT, "E", new BranchPair("I", "G")),
                new Gateway(Gateway.GatewayType.SPLIT, "E", new BranchPair("H", "G")),
                new Gateway(Gateway.GatewayType.JOIN, "F", new BranchPair("H", "I")),
                new Gateway(Gateway.GatewayType.JOIN, "F", new BranchPair("I", "G")),
                new Gateway(Gateway.GatewayType.JOIN, "F", new BranchPair("H", "G")),

                new Gateway(Gateway.GatewayType.SPLIT, "Q", new BranchPair("AP", "S")),

                new Gateway(Gateway.GatewayType.SPLIT, "S", new BranchPair("U", "Z")),
                new Gateway(Gateway.GatewayType.SPLIT, "S", new BranchPair("U", "AE")),
                new Gateway(Gateway.GatewayType.SPLIT, "S", new BranchPair("U", "AK")),
                new Gateway(Gateway.GatewayType.SPLIT, "S", new BranchPair("Z", "AE")),
                new Gateway(Gateway.GatewayType.SPLIT, "S", new BranchPair("Z", "AK")),
                new Gateway(Gateway.GatewayType.SPLIT, "S", new BranchPair("AE", "AK")),

                new Gateway(Gateway.GatewayType.SPLIT, "AE", new BranchPair("AG", "AH")),
                new Gateway(Gateway.GatewayType.SPLIT, "AE", new BranchPair("AG", "AI")),
                new Gateway(Gateway.GatewayType.SPLIT, "AE", new BranchPair("AG", "AJ")),
                new Gateway(Gateway.GatewayType.SPLIT, "AE", new BranchPair("AH", "AI")),
                new Gateway(Gateway.GatewayType.SPLIT, "AE", new BranchPair("AH", "AJ")),
                new Gateway(Gateway.GatewayType.SPLIT, "AE", new BranchPair("AI", "AJ")),
                new Gateway(Gateway.GatewayType.JOIN, "AF", new BranchPair("AG", "AH")),
                new Gateway(Gateway.GatewayType.JOIN, "AF", new BranchPair("AG", "AI")),
                new Gateway(Gateway.GatewayType.JOIN, "AF", new BranchPair("AG", "AJ")),
                new Gateway(Gateway.GatewayType.JOIN, "AF", new BranchPair("AH", "AI")),
                new Gateway(Gateway.GatewayType.JOIN, "AF", new BranchPair("AH", "AJ")),
                new Gateway(Gateway.GatewayType.JOIN, "AF", new BranchPair("AI", "AJ")),

                new Gateway(Gateway.GatewayType.SPLIT, "AK", new BranchPair("AM", "AN")),
                new Gateway(Gateway.GatewayType.SPLIT, "AK", new BranchPair("AM", "AO")),
                new Gateway(Gateway.GatewayType.SPLIT, "AK", new BranchPair("AN", "AO")),
                new Gateway(Gateway.GatewayType.JOIN, "AL", new BranchPair("AM", "AN")),
                new Gateway(Gateway.GatewayType.JOIN, "AL", new BranchPair("AM", "AO")),
                new Gateway(Gateway.GatewayType.JOIN, "AL", new BranchPair("AN", "AO")),

                new Gateway(Gateway.GatewayType.JOIN, "T", new BranchPair("V", "AA")),
                new Gateway(Gateway.GatewayType.JOIN, "T", new BranchPair("V", "AF")),
                new Gateway(Gateway.GatewayType.JOIN, "T", new BranchPair("V", "AL")),
                new Gateway(Gateway.GatewayType.JOIN, "T", new BranchPair("AA", "AF")),
                new Gateway(Gateway.GatewayType.JOIN, "T", new BranchPair("AA", "AL")),
                new Gateway(Gateway.GatewayType.JOIN, "T", new BranchPair("AF", "AL"))
        );
    }
}
