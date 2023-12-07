import de.cau.se.AggregationSink;
import de.cau.se.datastructure.BranchPair;
import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Gateway;
import de.cau.se.datastructure.Result;
import de.cau.se.map.result.MicroBatchRelationCountMap;
import de.cau.se.model.EventRelationLogger;
import de.cau.se.model.PrecisionChecker;
import de.cau.se.model.ModelUpdateService;
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

public class AggregationSinkTest {
    @Mock
    private Consumer<String, Result> consumer;
    private MinedProcessModel processModel;
    private MicroBatchRelationCountMap microBatchRelationCountMap;
    private AggregationSink testee;
    private EventRelationLogger eventRelationLogger;
    private PrecisionChecker precisionChecker;

    @BeforeEach
    void prepare() {
        openMocks(this);

        processModel = new MinedProcessModel();
        microBatchRelationCountMap = new MicroBatchRelationCountMap();

        eventRelationLogger = new EventRelationLogger();
        precisionChecker = new PrecisionChecker();

        testee = new AggregationSink(
                consumer,
                microBatchRelationCountMap,
                2,
                1,
                1,
                new ModelUpdateService(
                        0.5,
                        0.8,
                        processModel,
                        new MicroBatchRelationCountMap()),
                eventRelationLogger,
                precisionChecker,
                new SmallProcessModel());
    }

    @AfterEach
    void tearDown() {
        processModel = null;
        microBatchRelationCountMap = null;
        testee = null;
    }

    @Test
    public void testXor() {
        testee.receive(new Result(new DirectlyFollowsRelation("A", "B"), 5));
        testee.receive(new Result(new DirectlyFollowsRelation("B", "C"), 5));
        testee.receive(new Result(new DirectlyFollowsRelation("A", "D"), 7));
        testee.receive(new Result(new DirectlyFollowsRelation("D", "C"), 6));
        assertEquals(4, processModel.getCausalEvents().size());
        assertTrue(processModel.getChoiceGateways().contains(new Gateway(Gateway.GatewayType.SPLIT, "A", new BranchPair("B", "D"))));
        assertTrue(processModel.getChoiceGateways().contains(new Gateway(Gateway.GatewayType.JOIN, "C", new BranchPair("B", "D"))));

        assertEquals(2, processModel.getChoiceGateways().size());
        assertEquals(0, processModel.getParallelGateways().size());
    }

    @Test
    public void testParallel() {
        testee.receive(new Result(new DirectlyFollowsRelation("A", "B"), 5));
        testee.receive(new Result(new DirectlyFollowsRelation("B", "D"), 3));
        testee.receive(new Result(new DirectlyFollowsRelation("D", "B"), 3));
        testee.receive(new Result(new DirectlyFollowsRelation("B", "C"), 5));
        testee.receive(new Result(new DirectlyFollowsRelation("A", "D"), 5));
        testee.receive(new Result(new DirectlyFollowsRelation("D", "C"), 5));

        assertEquals(4, processModel.getCausalEvents().size());
        assertEquals(2, processModel.getParallelGateways().size());
        assertTrue(processModel.getParallelGateways().contains(new Gateway(Gateway.GatewayType.JOIN, "C", new BranchPair("B", "D"))));
        assertTrue(processModel.getParallelGateways().contains(new Gateway(Gateway.GatewayType.SPLIT, "A", new BranchPair("B", "D"))));
        assertEquals(0, processModel.getChoiceGateways().size());
    }

}
