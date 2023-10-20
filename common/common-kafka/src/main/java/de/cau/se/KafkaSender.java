package de.cau.se;

import de.cau.se.datastructure.Result;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Properties;


public class KafkaSender extends KafkaProducer<String, Result> {

    public KafkaSender() {
        super(getKafkaProperties());
    }

    private static Properties getKafkaProperties() {
        // create instance for properties to access producer configs
        Properties props = new Properties();
        props.put("bootstrap.servers", System.getenv("BOOTSTRAP_SERVER"));
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "de.cau.se.ResultSerializer");
        return props;
    }
}
