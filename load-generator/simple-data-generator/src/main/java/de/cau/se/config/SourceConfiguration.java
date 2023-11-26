package de.cau.se.config;

import de.cau.se.marker.Configuration;

/**
 * Interface for the configuration of
 */
public interface SourceConfiguration extends Configuration {
    String getBootstrapServer();

    String getTopicName();

    int getNumberPartitions();

    int getEventsPerSecond();

    String getEventLogFileName();
}
