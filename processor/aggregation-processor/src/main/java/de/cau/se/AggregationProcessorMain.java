package de.cau.se;

import de.cau.se.map.DirectlyFollowsMap;
import de.cau.se.map.TraceIdMap;

public class AggregationProcessorMain {

    public static void main(String[] args) {

        final String bootstrapServers = System.getenv("BOOTSTRAP_SERVER");
        final String topic = System.getenv("TOPIC_NAME");
        final String groupId = System.getenv("GROUP_ID");
        final Integer bucketSize = Integer.parseInt(System.getenv("BUCKET_SIZE"));

        new AggregationProcessor(
                new KafkaSender(),
                new KafkaEventConsumer<>(bootstrapServers, topic, groupId, EventDeserializer.class),
                new DirectlyFollowsMap(),
                new TraceIdMap(),
                bucketSize)
                .run();
    }
}
