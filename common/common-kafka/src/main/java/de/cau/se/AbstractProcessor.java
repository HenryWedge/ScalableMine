package de.cau.se;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Duration;

public abstract class AbstractProcessor<I,O> {

    private final Producer<String, O> producer;

    private final Consumer<String, I> consumer;

    public AbstractProcessor(final Producer<String, O> producer, final Consumer<String, I> consumer) {
        this.producer = producer;
        this.consumer = consumer;
    }

    protected abstract void receive(I input);

    protected void send(O output) {
        producer.send(new ProducerRecord<>("output", output));
    }

    public void run() {
        while (true) {
            ConsumerRecords<String, I> data = consumer.poll(Duration.ofMillis(1000));
            data.forEach(record -> receive(record.value()));
        }
    }
}
