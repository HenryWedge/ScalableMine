package io.spring.dataflow.sample.usagedetailsender;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import plg.generator.ProgressAdapter;
import plg.generator.log.LogGenerator;
import plg.generator.log.SimulationConfiguration;
import plg.generator.process.ProcessGenerator;
import plg.generator.process.RandomizationConfiguration;
import plg.model.Process;
@Configuration
@EnableIntegration
public class UsageDetailSender {
    private static final Logger logger = LoggerFactory.getLogger(UsageDetailSender.class);

    @Bean
    public Supplier<Message<List<Event>>> sendEvents() throws Exception {
        List<Event> eventLog = new ArrayList<>();
        fillEventLogWithDataFromFile(eventLog);

        final Queue<Event> eventQueue = new ArrayDeque<>(eventLog);
        if (eventQueue.isEmpty()) {
            System.out.println("Error: No events found to send");
            return () -> new GenericMessage<>(Collections.emptyList(), Collections.emptyMap());
        }

        final String numSensorsEnv = System.getenv("NUM_SENSORS");
        System.out.println("Reading numSensorsEnv: " + numSensorsEnv);

        final int numSensors = numSensorsEnv == null ? 1 : Integer.parseInt(numSensorsEnv);

        return () -> {
            final List<Event> outputList = new ArrayList<>();

            final Event firstEvent = eventQueue.peek();
            if (firstEvent == null) {
                logger.info("Finished. Sending null");
                return new GenericMessage<>(Collections.emptyList(), Collections.emptyMap());
            }

            final int firstTraceId = firstEvent.getTraceId();
            for ( int j = 0; j < numSensors; j++ ) {
                final Event event = eventQueue.peek();
                if (event == null || event.getTraceId() != firstTraceId) {
                    break;
                }
                outputList.add(eventQueue.poll());
            }

            final String logString = outputList
                .stream()
                .reduce("", (string, event) -> string + event.toString(), (a, b) -> a + b);

            logger.info("Sending event: {}", logString);
            return new GenericMessage<>(outputList, getHeaders(firstEvent.getTraceId()));
        };
    }

    private static void fillEventLogWithDataFromFile(final List<Event> eventLog) throws Exception {
        readProcess()
            .stream()
            .flatMap(Collection::stream)
            .map(UsageDetailSender::serializeXTrace)
            .flatMap(Collection::stream)
            .forEach(eventLog::add);
    }

    private Map<String, Object> getHeaders(final int traceId) {
        final HashMap<String, Object> headers = new HashMap<>();
        headers.put("partitionKey", traceId % 5);
        return headers;
    }

    private static List<Event> serializeXTrace(final XTrace trace) {
        return trace
            .stream()
            .map(event -> new Event(trace.hashCode(), event))
            .collect(Collectors.toList());
    }

    private static List<XLog> readProcess() throws Exception {
        XParser xesFileParser = new XesXmlParser();

        return xesFileParser.parse(UsageDetailSender.class
                                       .getClassLoader()
                                       .getResourceAsStream("event-log.xes"));

    }

    private static void writeProcess() throws Exception {
        Process p = new Process("");
        p.newStartEvent();
        p.newExclusiveGateway();
        ProcessGenerator.randomizeProcess(p, RandomizationConfiguration.BASIC_VALUES);

        LogGenerator logGenerator = new LogGenerator(p, new SimulationConfiguration(100000), new ProgressAdapter());

        System.out.println("serialization started");
        FileOutputStream fos = new FileOutputStream("process_data.txt");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        final List<Event> eventList = logGenerator
            .generateLog()
            .stream()
            .map(UsageDetailSender::serializeXTrace)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
        oos.writeObject(eventList);
    }
}
