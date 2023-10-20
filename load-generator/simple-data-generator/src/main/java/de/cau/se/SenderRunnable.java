package de.cau.se;

import de.cau.se.datastructure.Event;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Collection;

public class SenderRunnable implements Runnable {

    final Integer numberPartitions;

    final Integer eventsPerSecond;

    final String topicName;

    final Collection<Event> eventLog;

    final Producer producer;

    public SenderRunnable(Integer numberPartitions, Integer eventsPerSecond, String topicName, Collection<Event> eventLog, Producer producer) {
        this.numberPartitions = numberPartitions;
        this.eventsPerSecond = eventsPerSecond;
        this.topicName = topicName;
        this.eventLog = eventLog;
        this.producer = producer;
    }

    @Override
    public void run() {
        int noSentEvents = 0;
        for (Event event : eventLog) {
            noSentEvents++;
            System.out.println("noSentEvents: " + noSentEvents);

            producer.send(
                    new ProducerRecord<>(topicName,
                            Math.abs(event.getTraceId() % numberPartitions),
                            String.valueOf(event.getTraceId()),
                            event));
            System.out.println("Event sent:" + event);
            double sendInterval = 1000.0d / eventsPerSecond;
            long sleepMillis = (long) sendInterval;

            try {
                Thread.sleep(sleepMillis, ((int) (sendInterval * 1000) - (int) sleepMillis));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        producer.close();
    }
}
