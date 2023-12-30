package de.cau.se.map.result;

/**
 * Data structure used for the lossy counting filtering.
 * It uses the number of occurrences since the first observation of an activity (frequency) and
 * a delta which stores since which bucket the activity is observed.
 */
public class FrequencyDeltaPair {

    public FrequencyDeltaPair(int frequency, int delta) {
        this.frequency = frequency;
        this.delta = delta;
    }

    private int frequency;
    private int delta;

    public int getFrequency() {
        return frequency;
    }

    public int getDelta() {
        return delta;
    }
}
