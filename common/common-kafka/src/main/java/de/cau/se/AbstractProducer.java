package de.cau.se;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;


public class AbstractProducer<O> extends KafkaProducer<String, O> {

    public AbstractProducer(final String bootstrapServer, final Class<?> serializerClazz) {
        super(getKafkaProperties(bootstrapServer, serializerClazz));
    }

    private static Properties getKafkaProperties(final String bootstrapServer, final Class<?> serializerClazz) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, 120000);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializerClazz.getName());
        return props;
    }
}
