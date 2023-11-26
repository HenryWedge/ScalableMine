package de.cau.se;

import de.cau.se.config.EnvSourceConfiguration;
import de.cau.se.config.SourceConfiguration;

public class SimpleDataGeneratorMain {

    public static void main(String[] args) throws Exception {
        final SourceConfiguration config = new EnvSourceConfiguration();

        final SimpleDataGenerator simpleDataGenerator = new SimpleDataGenerator(config.getBootstrapServer(), EventSerializer.class);
        simpleDataGenerator.start(config.getTopicName(),
                                  config.getNumberPartitions(),
                                  config.getEventsPerSecond(),
                                  config.getEventLogFileName());
        System.out.println("Finished");
    }
}
