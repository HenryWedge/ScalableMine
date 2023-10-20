package de.cau.se;

import de.cau.se.map.ResultMap;
import de.cau.se.model.EventRelationLogger;
import de.cau.se.model.ModelUpdater;
import de.cau.se.model.PrecisionChecker;

import java.util.HashSet;

public class FilterSinkMain {

    public static void main(String[] args) {
        final String bootstrapServers = System.getenv("BOOTSTRAP_SERVER");
        final String groupId = System.getenv("GROUP_ID");
        final String topic = System.getenv("TOPIC_NAME");
        final Integer relevanceThreshold = Integer.parseInt(System.getenv("RELEVANCE_THRESHOLD"));
        final Integer aggregateCount = Integer.parseInt(System.getenv("AGGREGATE_COUNT"));
        final Double andThreshold = Double.parseDouble(System.getenv("AND_THRESHOLD"));
        final Double dependencyThreshold = Double.parseDouble(System.getenv("DEPENDENCY_THRESHOLD"));
        final FilterSink filterSink = new FilterSink(
                new KafkaEventConsumer<>(bootstrapServers, topic, groupId, ResultDeserializer.class),
                relevanceThreshold,
                aggregateCount,
                andThreshold,
                dependencyThreshold,
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new ResultMap(),
                new ModelUpdater(),
                new EventRelationLogger(),
                new PrecisionChecker());
        filterSink.run();
    }
}
