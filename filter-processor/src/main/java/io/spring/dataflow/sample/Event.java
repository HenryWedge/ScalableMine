package io.spring.dataflow.sample;

import java.io.Serializable;
import org.deckfour.xes.model.XEvent;
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
}
