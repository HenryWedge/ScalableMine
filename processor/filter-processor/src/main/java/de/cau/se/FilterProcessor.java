package de.cau.se;

import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Event;
import de.cau.se.datastructure.Result;
import de.cau.se.datastructure.TaggedRelation;
import de.cau.se.map.directlyfollows.DirectlyFollowsRelationCountMap;
import de.cau.se.map.result.MicroBatchRelationCountMap;
import de.cau.se.map.trace.TraceIdMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class FilterProcessor extends AbstractProcessor<Event, TaggedRelation> {

    private final MicroBatchRelationCountMap<DirectlyFollowsRelation> directlyFollowsCountMap;

    private final TraceIdMap traceIdEventMap;

    private final Integer bucketSize;

    private final Integer relevanceThreshold;

    private final Integer irrelevanceThreshold;

    public FilterProcessor(final AbstractProducer<TaggedRelation> sender,
                           final KafkaConsumer<Event> consumer,
                           final TraceIdMap traceIdEventMap,
                           final Integer bucketSize,
                           final Integer relevanceThreshold,
                           final Integer irrelevanceThreshold) {
        super(sender, consumer);
        this.directlyFollowsCountMap = new MicroBatchRelationCountMap<>();
        this.traceIdEventMap = traceIdEventMap;
        this.bucketSize = bucketSize;
        this.relevanceThreshold = relevanceThreshold;
        this.irrelevanceThreshold = irrelevanceThreshold;
    }

    @Override
    public void receive(final Event event) {
        updateTraceIdAndDirectlyFollowsMap(event);
        if (directlyFollowsCountMap.size() >= bucketSize) {
            sendResultMessage();
        }
    }

    private void updateTraceIdAndDirectlyFollowsMap(final Event event) {
        final String lastActivity = traceIdEventMap.accept(event.getTraceId(), event.getActivity());
        if (lastActivity != null) {
            directlyFollowsCountMap.insertOrUpdate(new DirectlyFollowsRelation(lastActivity, event.getActivity()), 1);
        }
    }

    private void sendResultMessage() {
        directlyFollowsCountMap.getRelevantRelations(relevanceThreshold).stream().map(relation -> new TaggedRelation(relation, TaggedRelation.Tag.RELEVANT)).forEach(super::send);
        directlyFollowsCountMap.getIrrelevantRelations(irrelevanceThreshold).stream().map(relation -> new TaggedRelation(relation, TaggedRelation.Tag.IRRELEVANT)).forEach(super::send);
        directlyFollowsCountMap.clear();
    }
}
