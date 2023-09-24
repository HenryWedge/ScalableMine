package io.spring.dataflow.sample;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
public class ConsumerDemo {
    private static final Logger log = LoggerFactory.getLogger(ConsumerDemo.class);

    public static void main(String[] args) {

        String bootstrapServers = System.getenv("BOOTSTRAP_SERVER");
        String groupId = System.getenv("GROUP_ID");
        String topic = System.getenv("TOPIC_NAME");

        // create consumer configs
        KafkaConsumer<String, Event> consumer = createConsumer(bootstrapServers, groupId);

        try {
            // subscribe consumer to our topic(s)
            consumer.subscribe(Arrays.asList(topic));

            final Map<DirectlyFollows, Integer> directlyFollowsCountMap = new HashMap<>();
            final Map<Integer, Event> traceIdEventMap = new HashMap<>();

            // poll for new data
            while ( true ) {
                ConsumerRecords<String, Event> records = consumer.poll(Duration.ofMillis(100));

                for ( ConsumerRecord<String, Event> record : records ) {
                    log.info("Key: " + record.key() + ", Value: " + record.value());
                    log.info("Partition: " + record.partition() + ", Offset:" + record.offset());

                    updateTraceIdAndDirectlyFollowsMap(directlyFollowsCountMap, traceIdEventMap, record
                        .value()
                        .getTraceId(), record.value());
                    if (determineDirectlyFollowsMapSize(directlyFollowsCountMap) >= Integer.parseInt(System.getenv("BUCKET_SIZE"))) {
                        sendResultMessage(directlyFollowsCountMap);
                    }

                }
            }

        } catch ( WakeupException e ) {
            log.info("Wake up exception!");
            // we ignore this as this is an expected exception when closing a consumer
        } catch ( Exception e ) {
            log.error("Unexpected exception", e);
        } finally {
            consumer.close(); // this will also commit the offsets if need be.
            log.info("The consumer is now gracefully closed.");
        }
    }

    private static void sendResultMessage(final Map<DirectlyFollows, Integer> directlyFollowsCountMap) {

        Producer<String, Result> producer = new KafkaProducer(getKafkaProperties());

        log.info("Sending results");

        final List<Result> resultList = directlyFollowsCountMap
            .entrySet()
            .stream()
            .map(entry -> new Result(entry
                                         .getKey(),
                                     entry.getValue()))
            .filter(result -> result.getCount() >= Integer.parseInt(System.getenv("RELEVANCE_THRESHOLD")))
            .collect(Collectors.toList());

        log.info("Bucket filled. Clearing directly follows map");
        directlyFollowsCountMap.clear();

        resultList.forEach(result -> producer.send(new ProducerRecord<>("output", result)));
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
                  "io.spring.dataflow.sample.ResultSerializer");

        return props;
    }

    private static KafkaConsumer<String, Event> createConsumer(final String bootstrapServers, final String groupId) {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EventDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // create consumer
        KafkaConsumer<String, Event> consumer = new KafkaConsumer<>(properties);

        // get a reference to the current thread
        registerShutdownHook(consumer);
        return consumer;
    }

    private static int determineDirectlyFollowsMapSize(final Map<DirectlyFollows, Integer> directlyFollowsCountMap) {
        return directlyFollowsCountMap
            .values()
            .stream()
            .reduce(Integer::sum)
            .orElse(0);
    }

    private static void updateTraceIdAndDirectlyFollowsMap(final Map<DirectlyFollows, Integer> directlyFollowsCountMap,
                                                           final Map<Integer, Event> traceIdEventMap,
                                                           final int traceId, final Event event) {
        log.info("Event {} received with trace {}", event.getActivity(), traceId);
        if (Objects.nonNull(traceIdEventMap.get(traceId))) {
            Event lastEvent = traceIdEventMap.get(traceId);
            traceIdEventMap.replace(traceId, lastEvent, event);
            if (!lastEvent
                .getActivity()
                .equals(event.getActivity())) {
                final DirectlyFollows directlyFollowsRelation = new DirectlyFollows(lastEvent.getActivity(), event.getActivity());
                addDirectlyFollowsToMap(directlyFollowsCountMap, directlyFollowsRelation);
            }
        } else {
            traceIdEventMap.put(event.getTraceId(), event);
        }
    }

    private static void addDirectlyFollowsToMap(final Map<DirectlyFollows, Integer> directlyFollowsCountMap,
                                                final DirectlyFollows directlyFollowsRelation) {

        if (directlyFollowsCountMap.containsKey(directlyFollowsRelation)) {
            final Integer newCount = directlyFollowsCountMap.get(directlyFollowsRelation) + 1;
            directlyFollowsCountMap.put(directlyFollowsRelation, newCount);
        } else {
            directlyFollowsCountMap.put(directlyFollowsRelation, 1);
        }
    }

    private static void registerShutdownHook(final KafkaConsumer<String, Event> consumer) {
        final Thread mainThread = Thread.currentThread();

        Runtime
            .getRuntime()
            .addShutdownHook(new Thread(() -> {
                log.info("Detected a shutdown, let's exit by calling consumer.wakeup()...");
                consumer.wakeup();

                // join the main thread to allow the execution of the code in the main thread
                try {
                    mainThread.join();
                } catch ( final InterruptedException e ) {
                    e.printStackTrace();
                }
            }));
    }
}
