import de.cau.se.AbstractProducer;
import de.cau.se.AggregationProcessor;
import de.cau.se.KafkaConsumer;
import de.cau.se.datastructure.Event;
import de.cau.se.map.directlyfollows.DirectlyFollowsRelationCountMap;
import de.cau.se.map.result.MicroBatchRelationCountMap;
import de.cau.se.map.trace.TraceIdMap;
import de.cau.se.model.MinedProcessModel;
import de.cau.se.model.ModelUpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(MockitoExtension.class)
public class FilterProcessorTest {

    private AggregationProcessor testee;
    @Mock
    private AbstractProducer sender;
    @Mock
    private KafkaConsumer consumer;
    private TraceIdMap traceIdMap;
    private DirectlyFollowsRelationCountMap directlyFollowsRelationCountMap;

    @BeforeEach
    void prepare() {
        openMocks(this);
        traceIdMap = new TraceIdMap();
        directlyFollowsRelationCountMap = new DirectlyFollowsRelationCountMap();
        testee = new AggregationProcessor(sender, consumer, directlyFollowsRelationCountMap, traceIdMap, 3, new ModelUpdateService(0.5d, 0.8d, new MicroBatchRelationCountMap<>()));
    }

    @Test
    void test() {
        testee.receive(new Event(1, "A"));

        //assertTrue(traceIdMap.containsKey(1));
        //assertEquals(new Event(1, "A"), traceIdMap.get(1), "");

        testee.receive(new Event(1, "B"));

        // assertTrue(traceIdMap.containsKey(1));
        // assertEquals(1, traceIdMap.size(), "");
        // assertEquals(new Event(1, "B"), traceIdMap.get(1), "");
        // assertEquals(directlyFollowsMap.get(new DirectlyFollows("A", "B")), 1, "");

        testee.receive(new Event(1, "C"));

        // assertTrue(traceIdMap.containsKey(1));
        // assertEquals(1, traceIdMap.size(), "");
        // assertEquals(new Event(1, "C"), traceIdMap.get(1), "");
        // assertEquals(directlyFollowsMap.get(new DirectlyFollows("A", "B")), 1, "");
        // assertEquals(directlyFollowsMap.get(new DirectlyFollows("B", "C")), 1, "");

        testee.receive(new Event(2, "A"));

        // assertTrue(traceIdMap.containsKey(1));
        // assertTrue(traceIdMap.containsKey(2));
        // assertEquals(2, traceIdMap.size(), "");
        // assertEquals(new Event(1, "C"), traceIdMap.get(1), "");
        // assertEquals(new Event(2, "A"), traceIdMap.get(2), "");
        // assertEquals(directlyFollowsMap.get(new DirectlyFollows("A", "B")), 1, "");
        // assertEquals(directlyFollowsMap.get(new DirectlyFollows("B", "C")), 1, "");
        // assertFalse(directlyFollowsMap.containsKey(new DirectlyFollows("C", "A")));

        testee.receive(new Event(1, "A"));

        //verify(sender.send(any()));
        //assertTrue(directlyFollowsMap.isEmpty());
    }
}
