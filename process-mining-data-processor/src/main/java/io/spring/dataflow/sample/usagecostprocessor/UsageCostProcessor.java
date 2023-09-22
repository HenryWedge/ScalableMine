package io.spring.dataflow.sample.usagecostprocessor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

@Configuration
public class UsageCostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(UsageCostProcessor.class);

    private static final int BUCKET_SIZE = 20;
    private static final int RELEVANCE_THRESHOLD = 2;

    @Bean
    public Function<Message<List<Event>>, Message<List<Result>>> processUsageCost() {

        final Map<DirectlyFollows, Integer> directlyFollowsCountMap = new HashMap<>();
        final Map<Integer, Event> traceIdEventMap = new HashMap<>();

        return eventList -> {
            if (eventList
                .getPayload()
                .isEmpty()) {
                logger.info("Finished");
                return new GenericMessage<>(Collections.emptyList(), getHeaders());
            }

            final List<Event> events = eventList.getPayload();
            final int traceId = events
                .get(0)
                .getTraceId();

            long startTime = System.currentTimeMillis();
            for ( Event event : events ) {
                updateTraceIdAndDirectlyFollowsMap(directlyFollowsCountMap, traceIdEventMap, traceId, event);
            }
            logger.info("Processing time: {}", (System.currentTimeMillis() - startTime));

            logMap("traceIdEventMap", traceIdEventMap);

            if (determineDirectlyFollowsMapSize(directlyFollowsCountMap) >= BUCKET_SIZE) {
                return sendResultMessage(directlyFollowsCountMap);
            }

            return sendEmptyMessage(directlyFollowsCountMap);
        };
    }

    private static void updateTraceIdAndDirectlyFollowsMap(final Map<DirectlyFollows, Integer> directlyFollowsCountMap, final Map<Integer, Event> traceIdEventMap,
                                  final int traceId, final Event event) {
        logger.info("Event {} received with trace {}", event.getActivity(), traceId);
        if (Objects.nonNull(traceIdEventMap.get(traceId))) {
            Event lastEvent = traceIdEventMap.get(traceId);
            traceIdEventMap.replace(traceId, lastEvent, event);
            if (!lastEvent
                .getActivity()
                .equals(event.getActivity())) {
                final DirectlyFollows directlyFollowsRelation = new DirectlyFollows(lastEvent.getActivity(), event.getActivity());
                addDirectlyFollowsToMap(directlyFollowsCountMap, directlyFollowsRelation);
            }
        } else {
            traceIdEventMap.put(event.getTraceId(), event);
        }
    }

    private GenericMessage<List<Result>> sendEmptyMessage(final Map<DirectlyFollows, Integer> directlyFollowsCountMap) {
        logger.info("Returning. Map size: {}", directlyFollowsCountMap.size());
        logMap("directlyFollowsCountMap", directlyFollowsCountMap);
        return new GenericMessage<>(Collections.emptyList(), getHeaders());
    }

    private GenericMessage<List<Result>> sendResultMessage(final Map<DirectlyFollows, Integer> directlyFollowsCountMap) {
        logger.info("Sending results");

        logMap("directlyFollowsCountMap", directlyFollowsCountMap);
        final List<Result> resultList = directlyFollowsCountMap
            .entrySet()
            .stream()
            .map(entry -> new Result(entry
                                         .getKey(),
                                     entry.getValue()))
            .filter(result -> result.getCount() >= RELEVANCE_THRESHOLD)
            .collect(Collectors.toList());

        logger.info("Bucket filled. Clearing directly follows map");
        directlyFollowsCountMap.clear();

        return new GenericMessage<>(resultList, getHeaders());
    }

    private int determineDirectlyFollowsMapSize(final Map<DirectlyFollows, Integer> directlyFollowsCountMap) {
        return directlyFollowsCountMap.values().stream().reduce(Integer::sum).orElse(0);
    }

    private Map<String, Object> getHeaders() {
        final HashMap<String, Object> headers = new HashMap<>();
        headers.put("sendTimeout", "1000");
        return headers;
    }

    private void logMap(final String mapName, final Map<?, ?> map) {
        map.forEach((k, v) -> logger.info("[{}] {}: {}", mapName, k, v));
    }

    private static void addDirectlyFollowsToMap(final Map<DirectlyFollows, Integer> directlyFollowsCountMap,
                                                final DirectlyFollows directlyFollowsRelation) {

        if (directlyFollowsCountMap.containsKey(directlyFollowsRelation)) {
            final Integer newCount = directlyFollowsCountMap.get(directlyFollowsRelation) + 1;
            directlyFollowsCountMap.put(directlyFollowsRelation, newCount);
        } else {
            directlyFollowsCountMap.put(directlyFollowsRelation, 1);
        }
    }
}
