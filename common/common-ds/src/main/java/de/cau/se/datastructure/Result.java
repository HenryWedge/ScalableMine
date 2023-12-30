package de.cau.se.datastructure;

/**
 * The result maps a {@link DirectlyFollowsRelation} to the number of its occurrences.
 */
public class Result {
    private DirectlyFollowsRelation directlyFollowsRelation;
    private Integer count;

    public Result() {
    }

    public Result(final DirectlyFollowsRelation directlyFollowsRelation, final Integer count) {
        this.directlyFollowsRelation = directlyFollowsRelation;
        this.count = count;
    }

    public DirectlyFollowsRelation getDirectlyFollows() {
        return directlyFollowsRelation;
    }

    public void setDirectlyFollows(final DirectlyFollowsRelation directlyFollowsRelation) {
        this.directlyFollowsRelation = directlyFollowsRelation;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(final Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Result{" +
                "directlyFollows=" + directlyFollowsRelation +
                ", count=" + count +
                '}';
    }
}
