import de.cau.se.*;
import de.cau.se.datastructure.DirectlyFollows;
import de.cau.se.datastructure.Event;
import de.cau.se.map.DirectlyFollowsMap;
import de.cau.se.map.TraceIdMap;
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
    private KafkaSender sender;
    @Mock
    private KafkaEventConsumer consumer;
    private TraceIdMap traceIdMap;
    private DirectlyFollowsMap directlyFollowsMap;

    @BeforeEach
    void prepare() {
        openMocks(this);
        traceIdMap = new TraceIdMap();
        directlyFollowsMap = new DirectlyFollowsMap();
        testee = new FilterProcessor(sender, consumer, directlyFollowsMap, traceIdMap, 3, 2);
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
        assertEquals(directlyFollowsMap.get(new DirectlyFollows("A", "B")), 1, "");

        testee.receive(new Event(1, "C"));

        assertTrue(traceIdMap.containsKey(1));
        assertEquals(1, traceIdMap.size(), "");
        assertEquals(new Event(1, "C"), traceIdMap.get(1), "");
        assertEquals(directlyFollowsMap.get(new DirectlyFollows("A", "B")), 1, "");
        assertEquals(directlyFollowsMap.get(new DirectlyFollows("B", "C")), 1, "");

        testee.receive(new Event(2, "A"));

        assertTrue(traceIdMap.containsKey(1));
        assertTrue(traceIdMap.containsKey(2));
        assertEquals(2, traceIdMap.size(), "");
        assertEquals(new Event(1, "C"), traceIdMap.get(1), "");
        assertEquals(new Event(2, "A"), traceIdMap.get(2), "");
        assertEquals(directlyFollowsMap.get(new DirectlyFollows("A", "B")), 1, "");
        assertEquals(directlyFollowsMap.get(new DirectlyFollows("B", "C")), 1, "");
        assertFalse(directlyFollowsMap.containsKey(new DirectlyFollows("C", "A")));

        testee.receive(new Event(1, "A"));

        //verify(sender.send(any()));
        //assertTrue(directlyFollowsMap.isEmpty());
    }
}
