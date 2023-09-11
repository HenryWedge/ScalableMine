package io.spring.dataflow.sample.usagecostprocessor;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
public class DirectlyFollows {
    private String predecessor;

    private String successor;

    public DirectlyFollows() {
    }

    public DirectlyFollows(final String predecessor, final String successor) {
        this.predecessor = predecessor;
        this.successor = successor;
    }

    public String getPredecessor() {
        return predecessor;
    }

    public String getSuccessor() {
        return successor;
    }

    public void setPredecessor(final String predecessor) {
        this.predecessor = predecessor;
    }

    public void setSuccessor(final String successor) {
        this.successor = successor;
    }

    @Override
    public String toString() {
        return "DirectlyFollows{" +
            "predecessor='" + predecessor + '\'' +
            ", successor='" + successor + '\'' +
            '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        final DirectlyFollows that = (DirectlyFollows) o;

        return new EqualsBuilder()
            .append(predecessor, that.predecessor)
            .append(successor, that.successor)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(predecessor)
            .append(successor)
            .toHashCode();
    }
}
