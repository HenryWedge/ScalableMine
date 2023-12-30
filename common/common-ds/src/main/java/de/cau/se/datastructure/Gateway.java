package de.cau.se.datastructure;

import de.cau.se.marker.Pattern;

import java.util.Objects;

public class Gateway implements Pattern {
    private GatewayType gatewayType;
    private String connectingActivity;
    private BranchPair branchPair;

    public Gateway() {
    }

    public Gateway(final GatewayType gatewayType, final String connectingActivity, final BranchPair branchPair) {
        this.gatewayType = gatewayType;
        this.connectingActivity = connectingActivity;
        this.branchPair = branchPair;
    }

    public boolean isBasedOnDirectlyFollowsRelation(final DirectlyFollowsRelation directlyFollowsRelation) {
        return (GatewayType.SPLIT == gatewayType
                && connectingActivity.equals(directlyFollowsRelation.getPredecessor())
                && (branchPair.getBranch1().equals(directlyFollowsRelation.getSuccessor())
                || branchPair.getBranch2().equals(directlyFollowsRelation.getSuccessor())))
                || (GatewayType.JOIN == gatewayType
                && connectingActivity.equals(directlyFollowsRelation.getSuccessor())
                && (branchPair.getBranch1().equals(directlyFollowsRelation.getPredecessor())
                || branchPair.getBranch2().equals(directlyFollowsRelation.getPredecessor())));
    }

    public enum GatewayType {
        SPLIT, JOIN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gateway gateway = (Gateway) o;
        return gatewayType == gateway.gatewayType && Objects.equals(connectingActivity, gateway.connectingActivity) && Objects.equals(branchPair, gateway.branchPair);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gatewayType, connectingActivity, branchPair);
    }

    @Override
    public String toString() {
        return "Gateway{" +
                "gatewayType=" + gatewayType +
                ", connectingActivity='" + connectingActivity + '\'' +
                ", branchPair=" + branchPair +
                '}';
    }

    public GatewayType getGatewayType() {
        return gatewayType;
    }

    public void setGatewayType(GatewayType gatewayType) {
        this.gatewayType = gatewayType;
    }

    public String getConnectingActivity() {
        return connectingActivity;
    }

    public void setConnectingActivity(String connectingActivity) {
        this.connectingActivity = connectingActivity;
    }

    public BranchPair getBranchPair() {
        return branchPair;
    }

    public void setBranchPair(BranchPair branchPair) {
        this.branchPair = branchPair;
    }
}
