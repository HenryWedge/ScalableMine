package de.cau.se.datastructure;

import org.deckfour.xes.model.XEvent;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a pair of trace id and activity as event.
 * The timestamp is omitted because it is not needed for the algorithms until now.
 */
public class Event implements Serializable {
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
    public String toString() {
        return "Event{" +
            "id=" + traceId +
            ", activity='" + activity + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return traceId == event.traceId && Objects.equals(activity, event.activity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(traceId, activity);
    }
}
