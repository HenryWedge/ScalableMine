package de.cau.se;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.cau.se.datastructure.Event;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a load generator. It sends data from a xes-event log to a kafka topic.
 * The messages are serialized as {@link de.cau.se.datastructure.Event}.
 * The load can be adjusted by the eventsPerSecond-Parameter in the start method.
 * Kafka specific configurations can be adjusted by topicName, numberOfPartitions, bootstrapServer
 */
public class SimpleDataGenerator extends AbstractProducer<Event> {
    public static final int MILLION = 1000000;
    public static final int BILLION = 1000000000;

    public SimpleDataGenerator(final String bootstrapServer, Class<?> serializerClazz) {
        super(bootstrapServer, serializerClazz);
    }

    /**
     * This method reads an xes-eventLog from the resources and sends them to a kafka topic
     *
     * @param topicName            name of the kafka topic where the events should be sent to
     * @param numberPartitions     number of kafka partitions - needed for proper load balancing between the topics
     * @param eventsPerSecond      adjusts the load produced
     * @param eventLogResourcePath path of the event log xes file relative to the resource folder
     * @throws Exception Kafka sending exceptions
     */
    public void start(final String topicName,
                      final int numberPartitions,
                      final int eventsPerSecond,
                      final String eventLogResourcePath) throws Exception {
        final List<Event> eventLog = buildEventLog(eventLogResourcePath);

        final List<Event> resultEventLog = new ArrayList<>();

        // replicate event log
        for (int i = 0; i < 120; i++) {
            resultEventLog.addAll(eventLog);
        }

        sendEvents(resultEventLog, topicName, numberPartitions, eventsPerSecond);
    }

    private void sendEvents(final List<Event> eventLog,
                             final String topic,
                             final int numberPartitions,
                             final int eventsPerSecond) {

        final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);
        final AtomicInteger counter = new AtomicInteger(MILLION * getReplicaNumber().orElse(0));
        final int periodBetweenSendActionsInNanoSeconds = BILLION / eventsPerSecond;

        scheduledExecutorService.scheduleAtFixedRate(() ->
        {
            final Event event = eventLog.get(counter.incrementAndGet());
            send(new ProducerRecord<>(topic, Math.abs(event.getTraceId() % numberPartitions),
                    String.valueOf(event.getTraceId()),
                    event));
        }, 0L, periodBetweenSendActionsInNanoSeconds, TimeUnit.NANOSECONDS);
    }

    private static Optional<Integer> getReplicaNumber() {
        final String hostname = System.getenv("HOSTNAME");
        final Pattern pattern = Pattern.compile(".*-(\\d+)");
        final Matcher matcher = pattern.matcher(hostname);
        if (matcher.matches()) {
            return Optional.of(Integer.parseInt(matcher.group(1).replace("-", "")));
        }
        return Optional.empty();
    }

    static List<Event> buildEventLog(final String eventLogResourcePath) throws Exception {
        XParser xesFileParser = new XesXmlParser();

        return xesFileParser
                .parse(SimpleDataGenerator.class
                        .getClassLoader()
                        .getResourceAsStream(eventLogResourcePath))
                .stream()
                .flatMap(Collection::stream)
                .flatMap(SimpleDataGenerator::serializeXTrace)
                .collect(Collectors.toList());
    }

    private static Stream<Event> serializeXTrace(final XTrace trace) {
        return trace.stream().map(event -> new Event(trace.hashCode(), event));
    }
}
