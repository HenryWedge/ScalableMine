package io.spring.dataflow.sample.usagecostlogger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.springframework.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import plg.model.Process;
import plg.model.gateway.ExclusiveGateway;
import plg.model.gateway.Gateway;
import plg.model.gateway.ParallelGateway;
import plg.model.sequence.Sequence;
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
                logger.info("Causal events: \n");
                findCausalEvents(resultMap).forEach(logDirectlyFollowingActions());

                logger.info("Parallel events: \n");
                findParallelEvents(resultMap).forEach(logDirectlyFollowingActions());

                logger.info("Exclusive events: \n");
                findChoiceEvents(resultMap).forEach(logDirectlyFollowingActions());
            });
        };
    }

    private static Consumer<DirectlyFollows> logDirectlyFollowingActions() {
        return follows -> logger.info(follows.toString() + "\n");
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
            .filter(directlyFollows -> !containsAndIsSignificant(resultMap, directlyFollows) &&
                !containsAndIsSignificant(resultMap, directlyFollows.getSwapped()))
            .collect(Collectors.toList());
    }

    public boolean containsAndIsSignificant(final Map<DirectlyFollows, Integer> map, final DirectlyFollows key) {
        return map.containsKey(key) && map.get(key) > 2;
    }

    public double calculatePrecision(Map<DirectlyFollows, Integer> resultMap) throws IOException, ClassNotFoundException {
        final Process originalProcess = readProcess("process_data.txt");

        final List<DirectlyFollows> causalEvents = findCausalEvents(resultMap);
        final List<DirectlyFollows> parallelEvents = findParallelEvents(resultMap);
        final List<DirectlyFollows> choiceEvents = findChoiceEvents(resultMap);

        final Set<Sequence> sequences = originalProcess.getSequences();
        final Map<? extends Class<? extends Gateway>, List<Gateway>> gatewayMap = originalProcess
            .getGateways()
            .stream()
            .collect(Collectors.groupingBy(Gateway::getClass));
        final List<Gateway> parallelGateways = gatewayMap.get(ParallelGateway.class);
        final List<Gateway> exclusiveGateways = gatewayMap.get(ExclusiveGateway.class);
        return 0.0d;
    }

    private static Process readProcess(final String filename) throws IOException, ClassNotFoundException {
        return (Process) new ObjectInputStream(UsageCostLogger.class
                                                   .getClassLoader()
                                                   .getResourceAsStream(filename)).readObject();
    }

}
