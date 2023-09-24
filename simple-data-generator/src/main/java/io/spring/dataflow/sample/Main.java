package io.spring.dataflow.sample;

import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XTrace;
public class Main {
    public static void main(String[] args) throws Exception {
        createEventLogAndSendEvents();
        while ( true ) {
            Thread.sleep(10000);
            System.out.println("Finished");
        }
    }

    private static void createEventLogAndSendEvents() throws Exception {
        String topicName = System.getenv("TOPIC_NAME");

        Properties props = getKafkaProperties();
        Producer<String, Event> producer = new KafkaProducer(props);
        final List<Event> eventLog = buildEventLog();
        sendEvents(topicName, producer, eventLog);
    }

    private static void sendEvents(final String topicName, final Producer<String, Event> producer, final List<Event> eventLog)
        throws InterruptedException {
        for ( Event event : eventLog ) {
            producer.send(
                new ProducerRecord<>(topicName,
                                     Math.abs(event.getTraceId() % Integer.parseInt(System.getenv("NUMBER_PARTITIONS"))),
                                     String.valueOf(event.getTraceId()),
                                     event));
            Thread.sleep(1000 / Integer.parseInt(System.getenv("EVENTS_PER_SECOND")));
        }
        producer.close();
    }

    private static List<Event> buildEventLog() throws Exception {
        XParser xesFileParser = new XesXmlParser();

        return xesFileParser
            .parse(Main.class
                       .getClassLoader()
                       .getResourceAsStream("event-log.xes"))
            .stream()
            .flatMap(Collection::stream)
            .flatMap(trace -> serializeXTrace(trace).stream())
            .collect(Collectors.toList());
    }

    private static Properties getKafkaProperties() {
        // create instance for properties to access producer configs
        Properties props = new Properties();

        //Assign localhost id
        props.put("bootstrap.servers", System.getenv("BOOTSTRAP_SERVER"));

        //Set acknowledgements for producer requests.
        props.put("acks", "all");

        //If the request fails, the producer can automatically retry,
        props.put("retries", 0);

        //Specify buffer size in config
        props.put("batch.size", 16384);

        //Reduce the no of requests less than 0
        props.put("linger.ms", 1);

        //The buffer.memory controls the total amount of memory available to the producer for buffering.
        props.put("buffer.memory", 33554432);

        props.put("key.serializer",
                  "org.apache.kafka.common.serialization.StringSerializer");

        props.put("value.serializer",
                  "io.spring.dataflow.sample.EventSerializer");

        return props;
    }

    private static List<Event> serializeXTrace(final XTrace trace) {
        return trace
            .stream()
            .map(event -> new Event(trace.hashCode(), event))
            .collect(Collectors.toList());
    }
}
