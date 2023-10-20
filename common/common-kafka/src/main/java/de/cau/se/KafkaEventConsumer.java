package de.cau.se;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;

public class KafkaEventConsumer<T> extends KafkaConsumer<String, T> {

    public KafkaEventConsumer(final String bootstrapServers, final String topic, final String groupId, final Class<?> deserializerClazz) {
        super(getProperties(bootstrapServers, groupId, deserializerClazz));
        super.subscribe(Collections.singletonList(topic));
        registerShutdownHook(this);
    }

    private static Properties getProperties(final String bootstrapServers, final String groupId, final Class<?> deserializerClazz) {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializerClazz.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return properties;
    }

    private void registerShutdownHook(KafkaConsumer<String, T> consumer) {
        final Thread mainThread = Thread.currentThread();
        Runtime
                .getRuntime()
                .addShutdownHook(new Thread(() -> {
                    consumer.wakeup();
                    // join the main thread to allow the execution of the code in the main thread
                    try {
                        mainThread.join();
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                }));
    }
}
