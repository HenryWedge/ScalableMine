package de.cau.se;

import de.cau.se.datastructure.Event;
import de.cau.se.datastructure.Result;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Duration;

public abstract class AbstractProcessor {

    private final Producer<String, Result> producer;

    private final Consumer<String, Event> consumer;

    public AbstractProcessor(final Producer<String, Result> producer, final Consumer<String, Event> consumer) {
        this.producer = producer;
        this.consumer = consumer;
    }

    protected abstract void receive(Event event);

    protected void send(Result sendData) {
        producer.send(new ProducerRecord<>("output", sendData));
    }

    public void run() {
        while (true) {
            ConsumerRecords<String, Event> data = consumer.poll(Duration.ofMillis(100));
            data.forEach(record -> receive(record.value()));
        }
    }
}
