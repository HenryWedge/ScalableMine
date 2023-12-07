package de.cau.se.datastructure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class DirectlyFollowsRelation {
    private String predecessor;
    private String successor;

    public DirectlyFollowsRelation() {
    }

    public DirectlyFollowsRelation(final String predecessor, final String successor) {
        this.predecessor = predecessor;
        this.successor = successor;
    }

    public String getPredecessor() {
        return predecessor;
    }

    public String getSuccessor() {
        return successor;
    }

    @JsonIgnore
    public DirectlyFollowsRelation getSwapped() {
        return new DirectlyFollowsRelation(this.successor, this.predecessor);
    }

    @Override
    public String toString() {
        return "de.cau.se.DirectlyFollows{" +
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

        final DirectlyFollowsRelation that = (DirectlyFollowsRelation) o;

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
