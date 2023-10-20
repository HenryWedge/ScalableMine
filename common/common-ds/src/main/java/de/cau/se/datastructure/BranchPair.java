package de.cau.se.datastructure;

import java.util.Objects;

public class BranchPair {
    private final String branch1;
    private final String branch2;

    public BranchPair(String branch1, String branch2) {
        this.branch1 = branch1;
        this.branch2 = branch2;
    }

    public String getBranch1() {
        return branch1;
    }

    public String getBranch2() {
        return branch2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BranchPair that = (BranchPair) o;
        return (Objects.equals(branch1, that.branch1) && Objects.equals(branch2, that.branch2))
            || (Objects.equals(branch1, that.branch2) && Objects.equals(branch2, that.branch1));
    }

    @Override
    public int hashCode() {
        return (Objects.hash(branch1) + Objects.hash(branch2)) % Integer.MAX_VALUE;
    }

    @Override
    public String toString() {
        return "BranchPair{" +
                "branch1='" + branch1 + '\'' +
                ", branch2='" + branch2 + '\'' +
                '}';
    }
}
