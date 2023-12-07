package de.cau.se;

import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Event;
import de.cau.se.datastructure.Result;
import de.cau.se.map.directlyfollows.DirectlyFollowsRelationCountMap;
import de.cau.se.map.trace.TraceIdMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class FilterProcessor extends AbstractProcessor<Event, Result> {

    private final DirectlyFollowsRelationCountMap directlyFollowsCountMap;

    private final TraceIdMap traceIdEventMap;

    private final Integer bucketSize;

    private final Integer relevanceThreshold;

    public FilterProcessor(final AbstractProducer<Result> sender,
                           final KafkaConsumer<Event> consumer,
                           final DirectlyFollowsRelationCountMap directlyFollowsCountMap,
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
        final String lastActivity = traceIdEventMap.accept(event.getTraceId(), event.getActivity());
        if (lastActivity != null) {
            directlyFollowsCountMap.accept(new DirectlyFollowsRelation(lastActivity, event.getActivity()));
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
