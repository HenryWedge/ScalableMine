package de.cau.se.map.directlyfollows;

import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Result;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DirectlyFollowsRelationCountMap extends HashMap<DirectlyFollowsRelation, Integer> {

    public void accept(final DirectlyFollowsRelation directlyFollowsRelation) {
        put(directlyFollowsRelation, containsKey(directlyFollowsRelation) ? get(directlyFollowsRelation) + 1 : 1);
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
