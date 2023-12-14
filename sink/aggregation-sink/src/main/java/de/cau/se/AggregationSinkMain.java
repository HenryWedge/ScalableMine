package de.cau.se;

import de.cau.se.map.result.MicroBatchRelationCountMap;
import de.cau.se.model.EventRelationLogger;
import de.cau.se.model.ModelUpdateService;
import de.cau.se.model.PrecisionChecker;
import de.cau.se.processmodel.ProcessModelFactory;


public class AggregationSinkMain {

    public static void main(String[] args) {
        final String bootstrapServers = System.getenv("BOOTSTRAP_SERVER");
        final String groupId = System.getenv("GROUP_ID");
        final String topic = System.getenv("TOPIC_NAME");
        final Double andThreshold = Double.parseDouble(System.getenv("AND_THRESHOLD"));
        final Double dependencyThreshold = Double.parseDouble(System.getenv("DEPENDENCY_THRESHOLD"));
        final int processModelVariant = Integer.parseInt(System.getenv("PROCESS_MODEL_VARIANT"));
        final int refreshRate = Integer.parseInt(System.getenv("REFRESH_RATE"));
        final boolean isIncremental = Boolean.parseBoolean(System.getenv("IS_INCREMENTAL"));
        final boolean usePrecisionMonitoring = Boolean.parseBoolean(System.getenv("USE_PRECISION_MONITORING"));
        final String precisionMonitoringUrl = System.getenv("PRECISION_MONITORING_URL");

        if (isIncremental) {
            final AggregationSink aggregationSink = new AggregationSink(
                    new KafkaConsumer<>(bootstrapServers, topic, groupId, TaggedRelationDeserializer.class),
                    refreshRate,
                    new ModelUpdateService(
                            andThreshold,
                            dependencyThreshold,
                            new MicroBatchRelationCountMap<>()),
                    new EventRelationLogger(),
                    new PrecisionChecker(usePrecisionMonitoring, precisionMonitoringUrl),
                    ProcessModelFactory.create(processModelVariant));

            aggregationSink.run();
        } else {
            final AggregationSinkLossyCounting aggregationSink = new AggregationSinkLossyCounting(
                    new KafkaConsumer<>(bootstrapServers, topic, groupId, ResultDeserializer.class),
                    new MicroBatchRelationCountMap<>(),
                    refreshRate,
                    new ModelUpdateService(
                            andThreshold,
                            dependencyThreshold,
                            new MicroBatchRelationCountMap<>()),
                    new EventRelationLogger(),
                    new PrecisionChecker(usePrecisionMonitoring, precisionMonitoringUrl),
                    ProcessModelFactory.create(processModelVariant));

            aggregationSink.run();
        }

    }
}
