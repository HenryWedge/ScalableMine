package de.cau.se.map;

import de.cau.se.datastructure.DirectlyFollows;
import de.cau.se.datastructure.Result;

import java.util.HashMap;
import java.util.Optional;

public class ResultMap extends HashMap<DirectlyFollows, Integer> {

    public void accept(final Result result) {
        DirectlyFollows directlyFollows = result.getDirectlyFollows();
        Integer count = result.getCount();
        put(directlyFollows, containsKey(directlyFollows) ? get(directlyFollows) + count : count);
    }

    public Integer get(final DirectlyFollows directlyFollows) {
        return Optional.ofNullable(super.get(directlyFollows)).orElse(0);
    }

    public void removeIrrelevantEvents(final Integer relevanceThreshold) {
        entrySet()
                .stream()
                .filter(entry -> entry.getValue() >= relevanceThreshold).forEach(entry -> remove(entry.getKey()));
    }
}
