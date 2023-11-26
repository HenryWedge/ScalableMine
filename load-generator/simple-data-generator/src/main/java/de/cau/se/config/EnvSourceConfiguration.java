package de.cau.se.config;

public class EnvSourceConfiguration implements SourceConfiguration {

    @Override
    public String getBootstrapServer() {
        return System.getenv("BOOTSTRAP_SERVER");
    }

    @Override
    public String getTopicName() {
        return System.getenv("TOPIC_NAME");
    }

    @Override
    public int getNumberPartitions() {
        return Integer.parseInt(System.getenv("NUMBER_PARTITIONS"));
    }

    @Override
    public int getEventsPerSecond() {
        return Integer.parseInt(System.getenv("EVENTS_PER_SECOND"));
    }

    @Override
    public String getEventLogFileName() {
        return System.getenv("EVENT_LOG_FILE_NAME");
    }
}
