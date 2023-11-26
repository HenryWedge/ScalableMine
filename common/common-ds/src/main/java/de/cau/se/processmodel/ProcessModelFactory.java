package de.cau.se.processmodel;

import java.util.Map;

public class ProcessModelFactory {

    private static final Map<Integer, ProcessModel> processModelMap =
            Map.of(
               1, new SmallProcessModel(),
               2, new MediumProcessModel()
            );

    public static ProcessModel create(final int variant) {
        return processModelMap.get(variant);
    }

}
