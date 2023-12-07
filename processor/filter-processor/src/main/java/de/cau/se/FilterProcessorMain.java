package de.cau.se;

import de.cau.se.map.directlyfollows.DirectlyFollowsRelationCountMap;
import de.cau.se.map.trace.TraceIdMap;

public class FilterProcessorMain {
    public static void main(String[] args) {
        final String bootstrapServer = System.getenv("BOOTSTRAP_SERVER");
        final String topic = System.getenv("TOPIC_NAME");
        final String groupId = System.getenv("GROUP_ID");
        final int bucketSize = Integer.parseInt(System.getenv("BUCKET_SIZE"));
        final int relevanceThreshold = Integer.parseInt(System.getenv("RELEVANCE_THRESHOLD"));
        final boolean isIncremental = Boolean.parseBoolean(System.getenv("IS_RELEVANT"));

        if (isIncremental) {
            new FilterProcessor(
                    new AbstractProducer<>(bootstrapServer, ResultSerializer.class),
                    new KafkaConsumer<>(bootstrapServer, topic, groupId, EventDeserializer.class),
                    new DirectlyFollowsRelationCountMap(),
                    new TraceIdMap(),
                    bucketSize,
                    relevanceThreshold).run();
        } else {
            new FilterProcessorLossyCounting(
                    new AbstractProducer<>(bootstrapServer, ResultSerializer.class),
                    new KafkaConsumer<>(bootstrapServer, topic, groupId, EventDeserializer.class),
                    bucketSize
            ).run();
        }
    }
}
