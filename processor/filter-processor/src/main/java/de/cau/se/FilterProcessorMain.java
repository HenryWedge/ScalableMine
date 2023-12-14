package de.cau.se;

import de.cau.se.map.trace.TraceIdMap;

public class FilterProcessorMain {
    public static void main(String[] args) {
        final String bootstrapServer = System.getenv("BOOTSTRAP_SERVER");
        final String topic = System.getenv("TOPIC_NAME");
        final String groupId = System.getenv("GROUP_ID");
        final int bucketSize = Integer.parseInt(System.getenv("BUCKET_SIZE"));
        final boolean isIncremental = Boolean.parseBoolean(System.getenv("IS_INCREMENTAL"));

        if (isIncremental) {
            final String[] relevanceEnv = System.getenv("RELEVANCE_THRESHOLDS").split("000");
            final int irrelevanceThreshold = Integer.parseInt(relevanceEnv[0]);
            final int relevanceThreshold = Integer.parseInt(relevanceEnv[1]);

            new FilterProcessor(
                    new AbstractProducer<>(bootstrapServer, TaggedRelationSerializer.class),
                    new KafkaConsumer<>(bootstrapServer, topic, groupId, EventDeserializer.class),
                    new TraceIdMap(),
                    bucketSize,
                    relevanceThreshold,
                    irrelevanceThreshold).run();
        } else {
            new FilterProcessorLossyCounting(
                    new AbstractProducer<>(bootstrapServer, ResultSerializer.class),
                    new KafkaConsumer<>(bootstrapServer, topic, groupId, EventDeserializer.class),
                    bucketSize
            ).run();
        }
    }
}
