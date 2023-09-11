package io.spring.dataflow.sample.usagecostprocessor;

public class Result {
    private DirectlyFollows directlyFollows;

    private Integer count;

    public Result() {
    }

    public Result(final DirectlyFollows directlyFollows, final Integer count) {
        this.directlyFollows = directlyFollows;
        this.count = count;
    }

    public DirectlyFollows getDirectlyFollows() {
        return directlyFollows;
    }

    public void setDirectlyFollows(final DirectlyFollows directlyFollows) {
        this.directlyFollows = directlyFollows;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(final Integer count) {
        this.count = count;
    }
}
