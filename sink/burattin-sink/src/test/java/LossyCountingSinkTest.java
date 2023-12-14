import de.cau.se.LossyCountingSink;
import de.cau.se.datastructure.*;
import de.cau.se.map.result.LossyCountingRelationCountMap;
import de.cau.se.model.EventRelationLogger;
import de.cau.se.model.MinedProcessModel;
import de.cau.se.model.ModelUpdateService;
import de.cau.se.model.PrecisionChecker;
import de.cau.se.processmodel.SmallProcessModel;
import org.apache.kafka.clients.consumer.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.openMocks;

public class LossyCountingSinkTest {
    @Mock
    private Consumer<String, Event> consumer;
    private MinedProcessModel processModel;
    private LossyCountingSink testee;
    private EventRelationLogger eventRelationLogger;
    private PrecisionChecker precisionChecker;

    @BeforeEach
    void prepare() {
        openMocks(this);

        processModel = new MinedProcessModel();

        eventRelationLogger = new EventRelationLogger();
        precisionChecker = new PrecisionChecker(false, "");

        testee = new LossyCountingSink(
                consumer,
                3,
                new ModelUpdateService(
                        0.3,
                        0.3,
                        new LossyCountingRelationCountMap<>()),
                eventRelationLogger,
                precisionChecker,
                1,
                new SmallProcessModel());
    }

    @AfterEach
    void tearDown() {
        processModel = null;
        testee = null;
    }


    @Test
    public void testCausalRelation() {
        testee.receive(new Event(1, "A"));
        testee.receive(new Event(1, "B"));
        testee.receive(new Event(1, "C"));
        testee.receive(new Event(2, "A"));
        testee.receive(new Event(2, "B"));
        testee.receive(new Event(2, "C"));
        testee.receive(new Event(3, "A"));
        testee.receive(new Event(3, "B"));
        testee.receive(new Event(3, "C"));

        assertEquals(2, processModel.getCausalEvents().size());
        assertEquals(0, processModel.getParallelGateways().size());
        assertTrue(processModel.getCausalEvents().contains(new DirectlyFollowsRelation("A", "B")));
        assertTrue(processModel.getCausalEvents().contains(new DirectlyFollowsRelation("B", "C")));
        assertEquals(0, processModel.getChoiceGateways().size());
    }

    @Test
    public void testParallel() {
        testee.receive(new Event(1, "A"));
        testee.receive(new Event(1, "B"));
        testee.receive(new Event(1, "C"));
        testee.receive(new Event(2, "A"));
        testee.receive(new Event(2, "D"));
        testee.receive(new Event(2, "B"));
        testee.receive(new Event(2, "C"));
        testee.receive(new Event(3, "A"));
        testee.receive(new Event(3, "D"));
        testee.receive(new Event(3, "C"));

        testee.receive(new Event(4, "A"));
        testee.receive(new Event(4, "B"));
        testee.receive(new Event(4, "C"));
        testee.receive(new Event(5, "A"));
        testee.receive(new Event(5, "D"));
        testee.receive(new Event(5, "B"));
        testee.receive(new Event(5, "C"));
        testee.receive(new Event(6, "A"));
        testee.receive(new Event(6, "D"));
        testee.receive(new Event(6, "C"));

        assertEquals(5, processModel.getCausalEvents().size());
        assertEquals(3, processModel.getParallelGateways().size());
        assertEquals(1, processModel.getChoiceGateways().size());
    }
}
