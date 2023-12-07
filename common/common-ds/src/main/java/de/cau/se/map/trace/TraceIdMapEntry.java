package de.cau.se.map.trace;

import de.cau.se.map.result.FrequencyDeltaPair;

/**
 * This class stores the last occurring event of an trace beside frequency and delta
 */
public class TraceIdMapEntry extends FrequencyDeltaPair {

    final String lastEvent;

    public TraceIdMapEntry(final String lastEvent, final int frequency, final int delta) {
        super(frequency, delta);
        this.lastEvent = lastEvent;
    }

    public String getLastEvent() {
        return lastEvent;
    }
}
