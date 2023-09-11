package io.spring.dataflow.sample.usagecostlogger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.springframework.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UsageCostLogger {
    private static final Logger logger = LoggerFactory.getLogger(UsageCostLogger.class);

    @Bean
    public Consumer<Message<List<Result>>> process() {
        final Map<DirectlyFollows, Integer> resultMap = new HashMap<>();

        return resultListMsg ->
        {
            List<Result> resultList = resultListMsg.getPayload();
            resultList.forEach(result -> {
                final DirectlyFollows directlyFollows = result.getDirectlyFollows();
                if (resultMap.containsKey(directlyFollows)) {
                    final Integer directlyFollowsCount = resultMap.get(directlyFollows);
                    resultMap.put(directlyFollows, directlyFollowsCount + 1);
                } else {
                    resultMap.put(directlyFollows, 1);
                }
                resultMap.forEach((k, v) -> logger.info("{}: {}", k, v));
                logger.info("-----------------------------------------");

            });
        };
    }

    public List<DirectlyFollows> findCausalEvents(Map<DirectlyFollows, Integer> resultMap) {
        return resultMap
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue() > 2)
            .filter(entry -> !resultMap.containsKey(new DirectlyFollows(entry
                                                                            .getKey()
                                                                            .getSuccessor(), entry
                                                                            .getKey()
                                                                            .getPredecessor())))
            .map(Entry::getKey)
            .collect(
                Collectors.toList());
    }

    public List<DirectlyFollows> findParallelEvents(Map<DirectlyFollows, Integer> resultMap) {
        return resultMap
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue() > 2)
            .filter(entry -> !resultMap.containsKey(new DirectlyFollows(entry
                                                                            .getKey()
                                                                            .getSuccessor(), entry
                                                                            .getKey()
                                                                            .getPredecessor())))
            .map(Entry::getKey)
            .collect(
                Collectors.toList());
    }

    public List<DirectlyFollows> findChoiceEvents(final Map<DirectlyFollows, Integer> resultMap) {
        return resultMap
            .keySet()
            .stream()
            .filter(directlyFollows -> !containsAndIsSignificant(resultMap, directlyFollows) && !containsAndIsSignificant(resultMap, directlyFollows.getSwapped()))
            .collect(Collectors.toList());
    }

    public boolean containsAndIsSignificant(final Map<DirectlyFollows, Integer> map, final DirectlyFollows key) {
        return map.containsKey(key) && map.get(key) > 2;
    }

    //public static void main(String[] args) {
    //    final UsageCostLogger usageCostLogger = new UsageCostLogger();
    //    final HashMap<DirectlyFollows, Integer> map = new HashMap<>();
    //    map.put(new DirectlyFollows("A", "B"), 5);
    //    map.put(new DirectlyFollows("B", "A"), 5);
    //    map.put(new DirectlyFollows("C", "D"), 5);
    //    map.put(new DirectlyFollows("A", "D"), 1);
    //    map.put(new DirectlyFollows("D", "A"), 3);
    //    usageCostLogger
    //        .findChoiceEvents(map)
    //        .forEach(System.out::println);
    //}
}
