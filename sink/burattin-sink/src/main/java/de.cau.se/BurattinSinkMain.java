package de.cau.se;

import de.cau.se.map.result.LossyCountingRelationCountMap;
import de.cau.se.map.result.CountBasedRelationCountMap;
import de.cau.se.model.EventRelationLogger;
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

        final int processModelVariant = Integer.parseInt(System.getenv("PROCESS_MODEL_VARIANT"));
        final boolean isIncremental = Boolean.parseBoolean(System.getenv("IS_INCREMENTAL"));
        final boolean usePrecisionMonitoring = Boolean.parseBoolean(System.getenv("USE_PRECISION_MONITORING"));
        final String precisionMonitoringUrl = System.getenv("PRECISION_MONITORING_URL");

        if (isIncremental) {
            final String[] relevanceEnv = System.getenv("RELEVANCE_THRESHOLDS").split("000");
            final int irrelevanceThreshold = Integer.parseInt(relevanceEnv[0]);
            final int relevanceThreshold = Integer.parseInt(relevanceEnv[1]);

            final LossyCountingSinkIncremental lossyCountingSink = new LossyCountingSinkIncremental(
                    new KafkaConsumer<>(bootstrapServers, topic, groupId, EventDeserializer.class),
                    bucketSize,
                    new ModelUpdateService(
                            andThreshold,
                            dependencyThreshold,
                            new CountBasedRelationCountMap<>()),
                    new EventRelationLogger(),
                    new PrecisionChecker(usePrecisionMonitoring, precisionMonitoringUrl),
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
                            new LossyCountingRelationCountMap<>()),
                    new EventRelationLogger(),
                    new PrecisionChecker(usePrecisionMonitoring, precisionMonitoringUrl),
                    refreshRate,
                    ProcessModelFactory.create(processModelVariant));
            lossyCountingSink.run();
        }
    }
}
