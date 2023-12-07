package de.cau.se;

import de.cau.se.map.result.MicroBatchRelationCountMap;
import de.cau.se.model.EventRelationLogger;
import de.cau.se.model.MinedProcessModel;
import de.cau.se.model.ModelUpdateService;
import de.cau.se.model.PrecisionChecker;
import de.cau.se.processmodel.ProcessModelFactory;


public class BurattinSinkMain {

    public static void main(String[] args) {
        final String bootstrapServers = System.getenv("BOOTSTRAP_SERVER");
        final String groupId = System.getenv("GROUP_ID");
        final String topic = System.getenv("TOPIC_NAME");
        final Double andThreshold = Double.parseDouble(System.getenv("AND_THRESHOLD"));
        final Double dependencyThreshold = Double.parseDouble(System.getenv("DEPENDENCY_THRESHOLD"));
        final int bucketSize = Integer.parseInt(System.getenv("BUCKET_SIZE"));
        final int refreshRate = Integer.parseInt(System.getenv("REFRESH_RATE"));
        final int relevanceThreshold = Integer.parseInt(System.getenv("RELEVANCE_THRESHOLD"));
        final int irrelevanceThreshold = Integer.parseInt(System.getenv("RELEVANCE_THRESHOLD"));
        final int processModelVariant = Integer.parseInt(System.getenv("PROCESS_MODEL_VARIANT"));
        final boolean isIncremental = Boolean.parseBoolean(System.getenv("IS_INCREMENTAL"));

        if (isIncremental) {
            final LossyCountingSinkIncremental lossyCountingSink = new LossyCountingSinkIncremental(
                    new KafkaConsumer<>(bootstrapServers, topic, groupId, EventDeserializer.class),
                    bucketSize,
                    new ModelUpdateService(
                            andThreshold,
                            dependencyThreshold,
                            new MinedProcessModel(),
                            new MicroBatchRelationCountMap()),
                    new EventRelationLogger(),
                    new PrecisionChecker(),
                    refreshRate,
                    relevanceThreshold,
                    irrelevanceThreshold,
                    ProcessModelFactory.create(processModelVariant));
            lossyCountingSink.run();
        } else {
            final LossyCountingSink lossyCountingSink = new LossyCountingSink(
                    new KafkaConsumer<>(bootstrapServers, topic, groupId, EventDeserializer.class),
                    bucketSize,
                    new ModelUpdateService(
                            andThreshold,
                            dependencyThreshold,
                            new MinedProcessModel(),
                            new MicroBatchRelationCountMap()),
                    new EventRelationLogger(),
                    new PrecisionChecker(),
                    refreshRate,
                    ProcessModelFactory.create(processModelVariant));
            lossyCountingSink.run();
        }
    }
}
