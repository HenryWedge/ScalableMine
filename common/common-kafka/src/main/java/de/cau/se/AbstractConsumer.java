package de.cau.se;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;

import java.time.Duration;

public abstract class AbstractConsumer<T> {

    private final Consumer<String, T> consumer;

    public AbstractConsumer(final Consumer<String, T> consumer) {
        this.consumer = consumer;
    }

    public abstract void receive(T event);

    public void run() {
        while (true) {
            ConsumerRecords<String, T> data = consumer.poll(Duration.ofMillis(100));
            data.forEach(record -> receive(record.value()));
        }
    }
}
