package de.cau.se;

import de.cau.se.model.*;
import de.cau.se.processmodel.ProcessModelFactory;

public class FilterSinkMain {

    public static void main(String[] args) {
        final String bootstrapServer = System.getenv("BOOTSTRAP_SERVER");
        final String groupId = System.getenv("GROUP_ID");
        final String topic = System.getenv("TOPIC_NAME");
        final Integer relevanceThreshold = Integer.parseInt(System.getenv("RELEVANCE_THRESHOLD"));
        final Integer aggregateCount = Integer.parseInt(System.getenv("AGGREGATE_COUNT"));
        final int processModelVersion = Integer.parseInt(System.getenv("PROCESS_MODEL_VARIANT"));

        final FilterSink filterSink = new FilterSink(
                new KafkaConsumer<>(bootstrapServer, topic, groupId, ProcessModelDeserializer.class),
                relevanceThreshold,
                aggregateCount,
                new CountBasedMinedProcessModel(),
                new EventRelationLogger(),
                new PrecisionChecker(),
                ProcessModelFactory.create(processModelVersion));

        filterSink.run();
    }
}
