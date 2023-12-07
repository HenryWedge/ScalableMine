package de.cau.se.map.result;

import de.cau.se.datastructure.DirectlyFollowsRelation;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ResultMap extends HashMap<DirectlyFollowsRelation, Integer> implements IResultMap<DirectlyFollowsRelation, Integer> {

    public void insertOrUpdate(final DirectlyFollowsRelation key) {
        insertOrUpdate(key, 1);
    }

    public void insertOrUpdate(final DirectlyFollowsRelation key, final Integer count) {
        if (containsKey(key)) {
            put(key, get(key) + count);
        } else {
            put(key, count);
        }
    }

    @Override
    public Integer getCountOf(DirectlyFollowsRelation key) {
        return Optional.ofNullable(get(key)).orElse(0);
    }

    public Set<DirectlyFollowsRelation> getRelationsWithCountGreaterThan(int threshold) {
        return keySet().stream().filter(key -> getCountOf(key) > threshold).collect(Collectors.toSet());
    }

    public Integer get(final DirectlyFollowsRelation directlyFollowsRelation) {
        return Optional.ofNullable(super.get(directlyFollowsRelation)).orElse(0);
    }

    public void removeIrrelevant(final Integer relevanceThreshold) {
        entrySet().removeIf(entry -> entry.getValue() <= relevanceThreshold);
    }

    @Override
    public Set<DirectlyFollowsRelation> getIrrelevant(Integer threshold) {
        return keySet().stream().filter(key -> get(key) <= threshold).collect(Collectors.toSet());
    }
}
