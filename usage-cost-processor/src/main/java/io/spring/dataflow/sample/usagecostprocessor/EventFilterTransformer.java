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

public class EventFilterTransformer {
    private static final Logger logger = LoggerFactory.getLogger(UsageCostProcessor.class);

    private final int bucketSize = 20;


    public Function<Message<Event>, Message<List<Result>>> processUsageCost() {

        final Map<DirectlyFollows, Integer> directlyFollowsCountMap = new HashMap<>();
        final Map<Integer, Event> traceIdEventMap = new HashMap<>();

        return eventMsg -> {
            if (eventMsg == null) {
                logger.info("Finished");
                return new GenericMessage<>(Collections.emptyList(), getHeaders());
            }

            final Event event = eventMsg.getPayload();

            final int traceId = event.getTraceId();
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

            logMap("traceIdEventMap", traceIdEventMap);

            if (directlyFollowsCountMap.size() > bucketSize) {
                logger.info("Bucket filled. Clearing directly follows map");
                directlyFollowsCountMap.clear();
            }

            if (directlyFollowsCountMap.size() == bucketSize) {
                logger.info("Sending results");

                logMap("directlyFollowsCountMap", directlyFollowsCountMap);
                final List<Result> collect = directlyFollowsCountMap
                    .entrySet()
                    .stream()
                    .map(entry -> new Result(entry
                                                 .getKey(),
                                             entry.getValue()))
                    .filter(result -> result.getCount() > 2)
                    .collect(Collectors.toList());
                return new GenericMessage<>(collect, getHeaders());
            }

            logger.info("Returning. Map size: {}", directlyFollowsCountMap.size());
            logMap("directlyFollowsCountMap", directlyFollowsCountMap);
            return new GenericMessage<>(Collections.emptyList(), getHeaders());
        };
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
