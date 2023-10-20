package de.cau.se.map;

import de.cau.se.datastructure.DirectlyFollows;
import de.cau.se.datastructure.Result;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DirectlyFollowsMap extends HashMap<DirectlyFollows, Integer> {

    public void accept(final DirectlyFollows directlyFollows) {
        put(directlyFollows, containsKey(directlyFollows) ? get(directlyFollows) + 1 : 1);
    }

    public List<Result> getAllRelevantEvents(final Integer relevanceThreshold) {
        return entrySet().stream()
                .filter(result -> result.getValue() >= relevanceThreshold)
                .map(entry -> new Result(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public int size() {
        return values()
                .stream()
                .reduce(Integer::sum)
                .orElse(0);
    }

}
