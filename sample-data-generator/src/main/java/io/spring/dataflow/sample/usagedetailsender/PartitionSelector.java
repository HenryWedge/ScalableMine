package io.spring.dataflow.sample.usagedetailsender;

import org.springframework.cloud.stream.binder.PartitionSelectorStrategy;
import org.springframework.context.annotation.Bean;

public class PartitionSelector implements PartitionSelectorStrategy {

    @Bean
    public int selectPartition(final Object key, final int partitionCount) {
        System.out.println("PartitionSelector: Class: " + key
            .getClass()
            .getSimpleName());
        return ((Event) key).getTraceId() % partitionCount;
    }
}
