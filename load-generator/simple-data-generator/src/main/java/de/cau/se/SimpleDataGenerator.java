package de.cau.se;

import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import de.cau.se.datastructure.Event;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleDataGenerator {
    private static final Logger log = LoggerFactory.getLogger(SimpleDataGenerator.class);

    public void createEventLogAndSendEvents() throws Exception {
        String topicName = System.getenv("TOPIC_NAME");

        Properties props = getKafkaProperties();
        Producer<String, Event> producer = new KafkaProducer(props);
        final List<Event> eventLog = buildEventLog();
        Integer numThreads = Integer.parseInt(System.getenv("NUM_THREADS"));
        Integer numberPartitions = Integer.parseInt(System.getenv("NUMBER_PARTITIONS"));
        Integer eventsPerSecond = Integer.parseInt(System.getenv("EVENTS_PER_SECOND"));
        sendEvents(numThreads, topicName, producer, eventLog, numberPartitions, eventsPerSecond);
    }

    private void sendEvents(final Integer numThreads,
                            final String topicName,
                            final Producer<String, Event> producer,
                            final List<Event> eventLog,
                            final Integer numberPartitions,
                            final Integer eventsPerSecond) {
        final Integer chunkSize = eventLog.size() / numThreads;
        for (int i = 0; i < numThreads; i++) {

            new Thread(
                    new SenderRunnable(numberPartitions,
                                       eventsPerSecond,
                                       topicName,
                                       eventLog.subList(i * chunkSize, (i + 1) * chunkSize - 1),
                                       producer),
                    "thread-" + i)
                    .start();
        }
    }

    private List<Event> buildEventLog() throws Exception {
        XParser xesFileParser = new XesXmlParser();

        return xesFileParser
                .parse(SimpleDataGenerator.class
                        .getClassLoader()
                        .getResourceAsStream("process-model-2.xes"))
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
                "de.cau.se.EventSerializer");

        return props;
    }

    private List<Event> serializeXTrace(final XTrace trace) {
        return trace
                .stream()
                .map(event -> new Event(trace.hashCode(), event))
                .collect(Collectors.toList());
    }
}
