package io.spring.dataflow.sample.usagecostlogger;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
