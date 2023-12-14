package de.cau.se;

import de.cau.se.model.*;
import de.cau.se.processmodel.ProcessModelFactory;

public class FilterSinkMain {

    public static void main(String[] args) {
        final String bootstrapServer = System.getenv("BOOTSTRAP_SERVER");
        final String groupId = System.getenv("GROUP_ID");
        final String topic = System.getenv("TOPIC_NAME");
        final boolean isIncremental = Boolean.parseBoolean(System.getenv("IS_INCREMENTAL"));
        final Integer refreshRate = Integer.parseInt(System.getenv("REFRESH_RATE"));
        final int processModelVersion = Integer.parseInt(System.getenv("PROCESS_MODEL_VARIANT"));
        final boolean usePrecisionMonitoring = Boolean.parseBoolean(System.getenv("USE_PRECISION_MONITORING"));
        final String precisionMonitoringUrl = System.getenv("PRECISION_MONITORING_URL");

        if (isIncremental) {
            final String[] relevanceEnv = System.getenv("RELEVANCE_THRESHOLDS").split("000");
            final int irrelevanceThreshold = Integer.parseInt(relevanceEnv[0]);
            final int relevanceThreshold = Integer.parseInt(relevanceEnv[1]);
            final FilterSink filterSink = new FilterSink(
                    new KafkaConsumer<>(bootstrapServer, topic, groupId, ProcessModelDeserializer.class),
                    relevanceThreshold,
                    irrelevanceThreshold,
                    refreshRate,
                    new CountBasedMinedProcessModel(),
                    new EventRelationLogger(),
                    new PrecisionChecker(usePrecisionMonitoring, precisionMonitoringUrl),
                    ProcessModelFactory.create(processModelVersion));

            filterSink.run();
        } else {
            final FilterSinkLossyCounting filterSink = new FilterSinkLossyCounting(
                    new KafkaConsumer<>(bootstrapServer, topic, groupId, ProcessModelDeserializer.class),
                    refreshRate,
                    new CountBasedMinedProcessModel(),
                    new EventRelationLogger(),
                    new PrecisionChecker(usePrecisionMonitoring, precisionMonitoringUrl),
                    ProcessModelFactory.create(processModelVersion));

            filterSink.run();
        }

    }
}
