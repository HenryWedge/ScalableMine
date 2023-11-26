package de.cau.se.processmodel;

import de.cau.se.datastructure.BranchPair;
import de.cau.se.datastructure.DirectlyFollows;
import de.cau.se.datastructure.Gateway;

import java.util.Set;

public class MediumProcessModel implements ProcessModel {

    @Override
    public Set<DirectlyFollows> getCausalEvents() {
        return Set.of(
                new DirectlyFollows("A", "C"),
                new DirectlyFollows("A", "D"),
                new DirectlyFollows("C", "B"),
                new DirectlyFollows("D", "B"),
                new DirectlyFollows("B", "E"),
                new DirectlyFollows("E", "H"),
                new DirectlyFollows("E", "I"),
                new DirectlyFollows("E", "G"),
                new DirectlyFollows("H", "F"),
                new DirectlyFollows("I", "F"),
                new DirectlyFollows("G", "F"),
                new DirectlyFollows("F", "J"),
                new DirectlyFollows("J", "K"),
                new DirectlyFollows("K", "L"),
                new DirectlyFollows("L", "N"),
                new DirectlyFollows("L", "O"),
                new DirectlyFollows("L", "P"),
                new DirectlyFollows("N", "M"),
                new DirectlyFollows("O", "M"),
                new DirectlyFollows("P", "M"),
                new DirectlyFollows("M", "Q"),
                new DirectlyFollows("Q", "AP"),
                new DirectlyFollows("Q", "S"),
                new DirectlyFollows("AP", "AR"),
                new DirectlyFollows("AP", "AS"),
                new DirectlyFollows("AP", "AT"),
                new DirectlyFollows("AR", "AQ"),
                new DirectlyFollows("AS", "AQ"),
                new DirectlyFollows("AT", "AQ"),
                new DirectlyFollows("AQ", "AU"),
                new DirectlyFollows("AU", "AV"),
                new DirectlyFollows("AV", "R"),
                new DirectlyFollows("S", "U"),
                new DirectlyFollows("S", "Z"),
                new DirectlyFollows("S", "AE"),
                new DirectlyFollows("S", "AK"),
                new DirectlyFollows("U", "W"),
                new DirectlyFollows("U", "X"),
                new DirectlyFollows("U", "Y"),
                new DirectlyFollows("W", "V"),
                new DirectlyFollows("X", "V"),
                new DirectlyFollows("Y", "V"),
                new DirectlyFollows("V", "T"),
                new DirectlyFollows("T", "R"),
                new DirectlyFollows("Z", "AB"),
                new DirectlyFollows("Z", "AC"),
                new DirectlyFollows("Z", "AD"),
                new DirectlyFollows("AB", "AA"),
                new DirectlyFollows("AC", "AA"),
                new DirectlyFollows("AD", "AA"),
                new DirectlyFollows("AA", "T"),
                new DirectlyFollows("AE", "AG"),
                new DirectlyFollows("AE", "AH"),
                new DirectlyFollows("AE", "AI"),
                new DirectlyFollows("AE", "AJ"),
                new DirectlyFollows("AG", "AF"),
                new DirectlyFollows("AH", "AF"),
                new DirectlyFollows("AI", "AF"),
                new DirectlyFollows("AJ", "AF"),
                new DirectlyFollows("AF", "T"),
                new DirectlyFollows("AK", "AM"),
                new DirectlyFollows("AK", "AN"),
                new DirectlyFollows("AK", "AO"),
                new DirectlyFollows("AM", "AL"),
                new DirectlyFollows("AN", "AL"),
                new DirectlyFollows("AO", "AL"),
                new DirectlyFollows("AL", "T"));
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
