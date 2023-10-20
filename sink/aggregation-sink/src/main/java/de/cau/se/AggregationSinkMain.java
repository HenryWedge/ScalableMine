package de.cau.se;

import de.cau.se.map.ResultMap;
import de.cau.se.model.EventRelationLogger;
import de.cau.se.model.ModelUpdater;
import de.cau.se.model.PrecisionChecker;

import java.util.HashSet;

public class AggregationSinkMain {
    public static void main(String[] args) {
        String bootstrapServers = System.getenv("BOOTSTRAP_SERVER");
        String groupId = System.getenv("GROUP_ID");
        String topic = System.getenv("TOPIC_NAME");
        final Double andThreshold = Double.parseDouble(System.getenv("AND_THRESHOLD"));
        final Double dependencyThreshold = Double.parseDouble(System.getenv("DEPENDENCY_THRESHOLD"));
        final AggregationSink aggregationSink = new AggregationSink(
                new KafkaEventConsumer<>(bootstrapServers, topic, groupId, ResultDeserializer.class),
                andThreshold,
                dependencyThreshold,
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new ResultMap(),
                new ModelUpdater(),
                new EventRelationLogger(),
                new PrecisionChecker());
        aggregationSink.run();
    }
}
