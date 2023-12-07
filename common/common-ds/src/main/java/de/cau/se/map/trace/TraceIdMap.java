package de.cau.se.map.trace;

import java.util.HashMap;

public class TraceIdMap extends HashMap<Integer, String> {

    public String accept(final Integer traceId, final String activity) {
        String lastActivity = get(traceId);

        if (lastActivity == null) {
            put(traceId, activity);
        } else {
            replace(traceId, lastActivity, activity);
        }

        return lastActivity;
    }

}
