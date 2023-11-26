package de.cau.se;

import de.cau.se.datastructure.DirectlyFollows;
import de.cau.se.datastructure.Event;
import de.cau.se.datastructure.Result;
import de.cau.se.map.DirectlyFollowsMap;
import de.cau.se.map.TraceIdMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class AggregationProcessor extends AbstractProcessor<Event, Result> {

    private static final Logger log = LoggerFactory.getLogger(AggregationProcessor.class);

    private final DirectlyFollowsMap directlyFollowsCountMap;

    private final TraceIdMap traceIdEventMap;

    private final Integer bucketSize;


    public AggregationProcessor(final AbstractProducer<Result> sender,
                                final KafkaConsumer<Event> consumer,
                                final DirectlyFollowsMap directlyFollowsCountMap,
                                final TraceIdMap traceIdEventMap,
                                final Integer bucketSize) {
        super(sender, consumer);
        this.directlyFollowsCountMap = directlyFollowsCountMap;
        this.traceIdEventMap = traceIdEventMap;
        this.bucketSize = bucketSize;
    }

    @Override
    protected void receive(Event event) {
        updateTraceIdAndDirectlyFollowsMap(event);
        if (directlyFollowsCountMap.size() >= bucketSize) {
            sendResultMessage();
        }
    }

    private void updateTraceIdAndDirectlyFollowsMap(final Event event) {
        System.out.printf("Event %s received with trace %d", event.getActivity(), event.getTraceId());

        final Event lastEvent = traceIdEventMap.accept(event);
        if (lastEvent != null) {
            directlyFollowsCountMap.accept(new DirectlyFollows(lastEvent.getActivity(), event.getActivity()));
        }
    }

    private void sendResultMessage() {
        final List<Result> resultList = directlyFollowsCountMap.entrySet().stream().map(entry -> new Result(entry.getKey(), entry.getValue())).collect(Collectors.toList());

        System.out.println("Bucket filled. Clearing directly follows map");
        directlyFollowsCountMap.clear();

        System.out.println("Sending results");

        resultList.forEach(super::send);
    }
}
