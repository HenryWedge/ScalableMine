import de.cau.se.*;
import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Event;
import de.cau.se.map.directlyfollows.DirectlyFollowsRelationCountMap;
import de.cau.se.map.result.LossyCountingRelationCountMap;
import de.cau.se.map.trace.TraceIdMap;
import de.cau.se.FilterProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static javatests.TestSupport.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(MockitoExtension.class)
public class FilterProcessorTest {

    private FilterProcessor testee;

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
        testee = new FilterProcessor(sender, consumer, traceIdMap, 5,3, 2);
    }

    @Test
    void test() {
        testee.receive(new Event(1, "A"));

        assertTrue(traceIdMap.containsKey(1));
        assertEquals(new Event(1, "A"), traceIdMap.get(1), "");

        testee.receive(new Event(1, "B"));

        assertTrue(traceIdMap.containsKey(1));
        assertEquals(1, traceIdMap.size(), "");
        assertEquals(new Event(1, "B"), traceIdMap.get(1), "");
        assertEquals(directlyFollowsRelationCountMap.get(new DirectlyFollowsRelation("A", "B")), 1, "");

        testee.receive(new Event(1, "C"));

        assertTrue(traceIdMap.containsKey(1));
        assertEquals(1, traceIdMap.size(), "");
        assertEquals(new Event(1, "C"), traceIdMap.get(1), "");
        assertEquals(directlyFollowsRelationCountMap.get(new DirectlyFollowsRelation("A", "B")), 1, "");
        assertEquals(directlyFollowsRelationCountMap.get(new DirectlyFollowsRelation("B", "C")), 1, "");

        testee.receive(new Event(2, "A"));

        assertTrue(traceIdMap.containsKey(1));
        assertTrue(traceIdMap.containsKey(2));
        assertEquals(2, traceIdMap.size(), "");
        assertEquals(new Event(1, "C"), traceIdMap.get(1), "");
        assertEquals(new Event(2, "A"), traceIdMap.get(2), "");
        assertEquals(directlyFollowsRelationCountMap.get(new DirectlyFollowsRelation("A", "B")), 1, "");
        assertEquals(directlyFollowsRelationCountMap.get(new DirectlyFollowsRelation("B", "C")), 1, "");
        assertFalse(directlyFollowsRelationCountMap.containsKey(new DirectlyFollowsRelation("C", "A")));

        testee.receive(new Event(1, "A"));

        //verify(sender.send(any()));
        //assertTrue(directlyFollowsMap.isEmpty());
    }
}
