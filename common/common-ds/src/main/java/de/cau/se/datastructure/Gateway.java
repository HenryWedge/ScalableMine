package de.cau.se.datastructure;

import java.util.Objects;

public class Gateway {

    private GatewayType gatewayType;
    private String connectingEvent;
    private BranchPair branchPair;

    public Gateway(final GatewayType gatewayType, final String connectingEvent, final BranchPair branchPair) {
        this.gatewayType = gatewayType;
        this.connectingEvent = connectingEvent;
        this.branchPair = branchPair;
    }

    public enum GatewayType {
        SPLIT, JOIN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gateway gateway = (Gateway) o;
        return gatewayType == gateway.gatewayType && Objects.equals(connectingEvent, gateway.connectingEvent) && Objects.equals(branchPair, gateway.branchPair);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gatewayType, connectingEvent, branchPair);
    }

    @Override
    public String toString() {
        return "Gateway{" +
                "gatewayType=" + gatewayType +
                ", connectingEvent='" + connectingEvent + '\'' +
                ", branchPair=" + branchPair +
                '}';
    }
}
