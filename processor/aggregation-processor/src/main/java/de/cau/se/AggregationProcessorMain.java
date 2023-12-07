package de.cau.se;

import de.cau.se.map.directlyfollows.DirectlyFollowsRelationCountMap;
import de.cau.se.map.result.ResultMap;
import de.cau.se.map.trace.TraceIdMap;
import de.cau.se.model.MinedProcessModel;
import de.cau.se.model.ModelUpdater;

public class AggregationProcessorMain {

    public static void main(String[] args) {
        final String bootstrapServer = System.getenv("BOOTSTRAP_SERVER");
        final String topic = System.getenv("TOPIC_NAME");
        final String groupId = System.getenv("GROUP_ID");
        final Integer bucketSize = Integer.parseInt(System.getenv("BUCKET_SIZE"));
        final Double andThreshold = Double.parseDouble(System.getenv("AND_THRESHOLD"));
        final Double dependencyThreshold = Double.parseDouble(System.getenv("DEPENDENCY_THRESHOLD"));

        //new AggregationProcessor(
        //        new AbstractProducer<>(bootstrapServer, ResultSerializer.class),
        //        new KafkaConsumer<>(bootstrapServer, topic, groupId, EventDeserializer.class),
        //        new DirectlyFollowsMap(),
        //        new TraceIdMap(),
        //        bucketSize)
        //        .run();

        new AggregationProcessor(
                new AbstractProducer<>(bootstrapServer, ProcessModelSerializer.class),
                new KafkaConsumer<>(bootstrapServer, topic, groupId, EventDeserializer.class),
                new DirectlyFollowsRelationCountMap(),
                new TraceIdMap(),
                bucketSize,
                new ResultMap(),
                new ModelUpdater(andThreshold, dependencyThreshold, new MinedProcessModel(), new ResultMap())).run();
    }
}
