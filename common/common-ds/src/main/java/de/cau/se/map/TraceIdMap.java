package de.cau.se.map;

import de.cau.se.datastructure.Event;

import java.util.HashMap;

public class TraceIdMap extends HashMap<Integer, Event> {

    public Event accept(final Event event) {
        int traceId = event.getTraceId();
        Event lastEvent = get(traceId);

        if (lastEvent == null) {
            put(traceId, event);
        } else {
            replace(traceId, lastEvent, event);
        }

        return lastEvent;
    }

}
