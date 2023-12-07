package de.cau.se;

import de.cau.se.model.*;
import de.cau.se.processmodel.ProcessModelFactory;

public class FilterSinkMain {

    public static void main(String[] args) {
        final String bootstrapServer = System.getenv("BOOTSTRAP_SERVER");
        final String groupId = System.getenv("GROUP_ID");
        final String topic = System.getenv("TOPIC_NAME");
        final Integer relevanceThreshold = Integer.parseInt(System.getenv("RELEVANCE_THRESHOLD"));
        final Integer irrelevanceThreshold = Integer.parseInt(System.getenv("IRRELEVANCE_THRESHOLD"));
        final boolean isIncremental = Boolean.parseBoolean(System.getenv("IS_INCREMENTAL"));
        final Integer refreshRate = Integer.parseInt(System.getenv("REFRESH_RATE"));
        final Integer bucketSize = Integer.parseInt(System.getenv("BUCKET_SIZE"));
        final int processModelVersion = Integer.parseInt(System.getenv("PROCESS_MODEL_VARIANT"));

        if (isIncremental) {
            final FilterSink filterSink = new FilterSink(
                    new KafkaConsumer<>(bootstrapServer, topic, groupId, ProcessModelDeserializer.class),
                    relevanceThreshold,
                    irrelevanceThreshold,
                    refreshRate,
                    new CountBasedMinedProcessModel(),
                    new EventRelationLogger(),
                    new PrecisionChecker(),
                    ProcessModelFactory.create(processModelVersion));

            filterSink.run();
        } else {
            final FilterSinkLossyCounting filterSink = new FilterSinkLossyCounting(
                    new KafkaConsumer<>(bootstrapServer, topic, groupId, ProcessModelDeserializer.class),
                    refreshRate,
                    bucketSize,
                    new CountBasedMinedProcessModel(),
                    new EventRelationLogger(),
                    new PrecisionChecker(),
                    ProcessModelFactory.create(processModelVersion));

            filterSink.run();
        }

    }
}
