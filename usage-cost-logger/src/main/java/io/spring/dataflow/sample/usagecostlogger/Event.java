package io.spring.dataflow.sample.usagecostlogger;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.deckfour.xes.model.XEvent;

public class Event {
    private int traceId;

    private String activity;

    public Event() {
    }

    public Event(final int traceId, final String activity) {
        this.traceId = traceId;
        this.activity = activity;
    }

    public Event(final int traceId, final XEvent event) {
        this(traceId, event
            .getAttributes()
            .get("concept:name")
            .toString()
            .replace("Activity ", ""));
    }

    public int getTraceId() {
        return traceId;
    }

    public String getActivity() {
        return activity;
    }

    public void setTraceId(final int traceId) {
        this.traceId = traceId;
    }

    public void setActivity(final String activity) {
        this.activity = activity;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        final Event event = (Event) o;

        return new EqualsBuilder()
            .append(traceId, event.traceId)
            .append(activity, event.activity)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(traceId)
            .append(activity)
            .toHashCode();
    }

    @Override
    public String toString() {
        return "Event{" +
            "id=" + traceId +
            ", activity='" + activity + '\'' +
            '}';
    }
}

