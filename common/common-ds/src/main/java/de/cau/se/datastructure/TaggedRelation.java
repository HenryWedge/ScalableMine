package de.cau.se.datastructure;

public class TaggedRelation {

    private DirectlyFollowsRelation directlyFollowsRelation;
    private Tag tag;

    public TaggedRelation() {
    }

    public TaggedRelation(final DirectlyFollowsRelation directlyFollowsRelation, final Tag tag) {
        this.directlyFollowsRelation = directlyFollowsRelation;
        this.tag = tag;
    }

    public DirectlyFollowsRelation getDirectlyFollows() {
        return directlyFollowsRelation;
    }

    public void setDirectlyFollows(final DirectlyFollowsRelation directlyFollowsRelation) {
        this.directlyFollowsRelation = directlyFollowsRelation;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "TaggedRelation{" +
                "directlyFollowsRelation=" + directlyFollowsRelation +
                ", tag=" + tag +
                '}';
    }

    public enum Tag {
        RELEVANT, IRRELEVANT
    }
}
