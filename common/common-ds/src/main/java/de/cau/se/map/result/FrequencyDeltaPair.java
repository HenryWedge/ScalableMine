package de.cau.se.map.result;

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
