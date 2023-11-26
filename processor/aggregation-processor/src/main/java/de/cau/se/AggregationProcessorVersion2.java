package de.cau.se;

import de.cau.se.datastructure.DirectlyFollows;
import de.cau.se.datastructure.Event;
import de.cau.se.datastructure.Result;
import de.cau.se.map.DirectlyFollowsMap;
import de.cau.se.map.ResultMap;
import de.cau.se.map.TraceIdMap;
import de.cau.se.model.MinedProcessModel;
import de.cau.se.model.ModelUpdater;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class AggregationProcessorVersion2 extends AbstractProcessor<Event, MinedProcessModel> {

    private final DirectlyFollowsMap directlyFollowsCountMap;

    private final TraceIdMap traceIdEventMap;

    private final Integer bucketSize;

    private final ResultMap resultMap;

    private final ModelUpdater modelUpdater;

    public AggregationProcessorVersion2(final AbstractProducer<MinedProcessModel> sender,
                                        final KafkaConsumer<Event> consumer,
                                        final DirectlyFollowsMap directlyFollowsCountMap,
                                        final TraceIdMap traceIdEventMap,
                                        final Integer bucketSize,
                                        final ResultMap resultMap,
                                        final ModelUpdater modelUpdater) {
        super(sender, consumer);
        this.directlyFollowsCountMap = directlyFollowsCountMap;
        this.traceIdEventMap = traceIdEventMap;
        this.bucketSize = bucketSize;
        this.resultMap = resultMap;
        this.modelUpdater = modelUpdater;
    }

    @Override
    public void receive(Event event) {
        updateTraceIdAndDirectlyFollowsMap(event);
        if (directlyFollowsCountMap.size() >= bucketSize) {
            processBucket();
        }
    }

    private void updateTraceIdAndDirectlyFollowsMap(final Event event) {
        final Event lastEvent = traceIdEventMap.accept(event);
        if (lastEvent != null) {
            directlyFollowsCountMap.accept(new DirectlyFollows(lastEvent.getActivity(), event.getActivity()));
        }
    }

    private void processBucket() {
        directlyFollowsCountMap
                .entrySet()
                .stream()
                .map(entry -> new Result(entry.getKey(), entry.getValue()))
                .forEach(resultMap::accept);

        modelUpdater.findCausalEvents(resultMap, directlyFollowsCountMap.keySet());

        final Set<String> newDiscoveredActivities = directlyFollowsCountMap.keySet()
                .stream()
                .map(df -> Set.of(df.getPredecessor(), df.getSuccessor()))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        modelUpdater.findJoins(newDiscoveredActivities, resultMap);
        modelUpdater.findSplits(newDiscoveredActivities, resultMap);

        super.send(modelUpdater.getProcessModel());

        System.out.println("Bucket filled. Clearing directly follows map");
        directlyFollowsCountMap.clear();

        System.out.println("Sending results");
    }
}
