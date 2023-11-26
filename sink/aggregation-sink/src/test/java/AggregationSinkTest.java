import de.cau.se.datastructure.BranchPair;
import de.cau.se.datastructure.DirectlyFollows;
import de.cau.se.datastructure.Gateway;
import de.cau.se.datastructure.Result;
import de.cau.se.map.ResultMap;
import de.cau.se.model.EventRelationLogger;
import de.cau.se.model.PrecisionChecker;
import de.cau.se.AggregationSink;
import de.cau.se.model.ModelUpdater;
import de.cau.se.model.MinedProcessModel;
import de.cau.se.processmodel.SmallProcessModel;
import org.apache.kafka.clients.consumer.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.openMocks;

// TODO here fix tests
public class AggregationSinkTest {
    @Mock
    private Consumer<String, Result> consumer;
    private MinedProcessModel processModel;
    private ResultMap resultMap;
    private AggregationSink testee;
    private ModelUpdater modelUpdater;
    private EventRelationLogger eventRelationLogger;
    private PrecisionChecker precisionChecker;

    @BeforeEach
    void prepare() {
        openMocks(this);

        processModel = new MinedProcessModel();
        resultMap = new ResultMap();

        modelUpdater = new ModelUpdater(
                0.5,
                0.8,
                processModel);

        eventRelationLogger = new EventRelationLogger();
        precisionChecker = new PrecisionChecker();

        testee = new AggregationSink(
                consumer,
                resultMap,
                new ModelUpdater(
                        0.5,
                        0.8,
                        processModel),
                eventRelationLogger,
                precisionChecker,
                new SmallProcessModel());
    }

    @AfterEach
    void tearDown() {
        processModel = null;
        resultMap = null;
        testee = null;
    }

    @Test
    public void testXor() {
        testee.receive(new Result(new DirectlyFollows("A", "B"), 5));
        testee.receive(new Result(new DirectlyFollows("B", "C"), 5));
        testee.receive(new Result(new DirectlyFollows("A", "D"), 7));
        testee.receive(new Result(new DirectlyFollows("D", "C"), 6));
        assertEquals(4, processModel.getCausalEvents().size());
        assertTrue(processModel.getChoiceGateways().contains(new Gateway(Gateway.GatewayType.SPLIT, "A", new BranchPair("B", "D"))));
        assertTrue(processModel.getChoiceGateways().contains(new Gateway(Gateway.GatewayType.JOIN, "C", new BranchPair("B", "D"))));

        assertEquals(2, processModel.getChoiceGateways().size());
        assertEquals(0, processModel.getParallelGateways().size());
    }

    @Test
    public void testParallel() {
        testee.receive(new Result(new DirectlyFollows("A", "B"), 5));
        testee.receive(new Result(new DirectlyFollows("B", "D"), 3));
        testee.receive(new Result(new DirectlyFollows("D", "B"), 3));
        testee.receive(new Result(new DirectlyFollows("B", "C"), 5));
        testee.receive(new Result(new DirectlyFollows("A", "D"), 5));
        testee.receive(new Result(new DirectlyFollows("D", "C"), 5));

        assertEquals(4, processModel.getCausalEvents().size());
        assertEquals(2, processModel.getParallelGateways().size());
        assertTrue(processModel.getParallelGateways().contains(new Gateway(Gateway.GatewayType.JOIN, "C", new BranchPair("B", "D"))));
        assertTrue(processModel.getParallelGateways().contains(new Gateway(Gateway.GatewayType.SPLIT, "A", new BranchPair("B", "D"))));
        assertEquals(0, processModel.getChoiceGateways().size());
    }

}
