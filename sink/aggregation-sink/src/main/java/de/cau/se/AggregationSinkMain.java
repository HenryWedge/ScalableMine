package de.cau.se;

import de.cau.se.map.result.ResultMap;
import de.cau.se.model.EventRelationLogger;
import de.cau.se.model.MinedProcessModel;
import de.cau.se.model.ModelUpdater;
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
        final int aggregateCount = Integer.parseInt(System.getenv("AGGREGATE_COUNT"));
        final int relevanceThreshold = Integer.parseInt(System.getenv("RELEVANCE_THRESHOLD"));

        final AggregationSink aggregationSink = new AggregationSink(
                new KafkaConsumer<>(bootstrapServers, topic, groupId, ResultDeserializer.class),
                new ResultMap(),
                aggregateCount,
                relevanceThreshold,
                new ModelUpdater(
                        andThreshold,
                        dependencyThreshold,
                        new MinedProcessModel(), new ResultMap()),
                new EventRelationLogger(),
                new PrecisionChecker(),
                ProcessModelFactory.create(processModelVariant));

        aggregationSink.run();
    }
}
