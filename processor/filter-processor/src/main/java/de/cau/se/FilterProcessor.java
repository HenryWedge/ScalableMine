package de.cau.se;

import de.cau.se.datastructure.DirectlyFollows;
import de.cau.se.datastructure.Event;
import de.cau.se.datastructure.Result;
import de.cau.se.map.DirectlyFollowsMap;
import de.cau.se.map.TraceIdMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class FilterProcessor extends AbstractProcessor {

    private static final Logger log = LoggerFactory.getLogger(FilterProcessor.class);

    private final DirectlyFollowsMap directlyFollowsCountMap;

    private final TraceIdMap traceIdEventMap;

    private final Integer bucketSize;

    private final Integer relevanceThreshold;

    public FilterProcessor(final KafkaSender sender,
                           final KafkaEventConsumer consumer,
                           final DirectlyFollowsMap directlyFollowsCountMap,
                           final TraceIdMap traceIdEventMap,
                           final Integer bucketSize,
                           final Integer relevanceThreshold) {
        super(sender, consumer);
        this.directlyFollowsCountMap = directlyFollowsCountMap;
        this.traceIdEventMap = traceIdEventMap;
        this.bucketSize = bucketSize;
        this.relevanceThreshold = relevanceThreshold;
    }

    @Override
    public void receive(final Event event) {
        updateTraceIdAndDirectlyFollowsMap(event);
        if (directlyFollowsCountMap.size() >= bucketSize) {
            sendResultMessage(relevanceThreshold);
        }
    }

    private void updateTraceIdAndDirectlyFollowsMap(final Event event) {
        System.out.printf("Event %s received with trace %d", event.getActivity(), event.getTraceId());

        final Event lastEvent = traceIdEventMap.accept(event);
        if (lastEvent != null) {
            directlyFollowsCountMap.accept(new DirectlyFollows(lastEvent.getActivity(), event.getActivity()));
        }
    }

    private void sendResultMessage(final Integer relevanceThreshold) {
        final List<Result> resultList = directlyFollowsCountMap.getAllRelevantEvents(relevanceThreshold);

        System.out.println("Bucket filled. Clearing directly follows map");
        directlyFollowsCountMap.clear();

        System.out.println("Sending results");
        resultList.forEach(super::send);
    }
}
