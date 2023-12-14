import de.cau.se.AggregationSink;
import de.cau.se.datastructure.*;
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
    private Consumer<String, TaggedRelation> consumer;
    private MinedProcessModel processModel;
    private MicroBatchRelationCountMap<DirectlyFollowsRelation> microBatchRelationCountMap;
    private AggregationSink testee;
    private EventRelationLogger eventRelationLogger;
    private PrecisionChecker precisionChecker;

    @BeforeEach
    void prepare() {
        openMocks(this);

        processModel = new MinedProcessModel();
        microBatchRelationCountMap = new MicroBatchRelationCountMap<>();

        eventRelationLogger = new EventRelationLogger();
        precisionChecker = new PrecisionChecker(false, "");

        testee = new AggregationSink(
                consumer,
                3,
                new ModelUpdateService(
                        0.5,
                        0.8,
                        new MicroBatchRelationCountMap<>()),
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
        testee.receive(new TaggedRelation(new DirectlyFollowsRelation("A", "B"), TaggedRelation.Tag.RELEVANT));
        testee.receive(new TaggedRelation(new DirectlyFollowsRelation("B", "C"), TaggedRelation.Tag.RELEVANT));
        testee.receive(new TaggedRelation(new DirectlyFollowsRelation("A", "D"), TaggedRelation.Tag.RELEVANT));
        testee.receive(new TaggedRelation(new DirectlyFollowsRelation("D", "C"), TaggedRelation.Tag.RELEVANT));
        assertEquals(4, processModel.getCausalEvents().size());
        assertTrue(processModel.getChoiceGateways().contains(new Gateway(Gateway.GatewayType.SPLIT, "A", new BranchPair("B", "D"))));
        assertTrue(processModel.getChoiceGateways().contains(new Gateway(Gateway.GatewayType.JOIN, "C", new BranchPair("B", "D"))));

        assertEquals(2, processModel.getChoiceGateways().size());
        assertEquals(0, processModel.getParallelGateways().size());
    }

    @Test
    public void testParallel() {
        testee.receive(new TaggedRelation(new DirectlyFollowsRelation("A", "B"), TaggedRelation.Tag.RELEVANT));
        testee.receive(new TaggedRelation(new DirectlyFollowsRelation("B", "D"), TaggedRelation.Tag.RELEVANT));
        testee.receive(new TaggedRelation(new DirectlyFollowsRelation("D", "B"), TaggedRelation.Tag.RELEVANT));
        testee.receive(new TaggedRelation(new DirectlyFollowsRelation("B", "C"), TaggedRelation.Tag.RELEVANT));
        testee.receive(new TaggedRelation(new DirectlyFollowsRelation("A", "D"), TaggedRelation.Tag.RELEVANT));
        testee.receive(new TaggedRelation(new DirectlyFollowsRelation("D", "C"), TaggedRelation.Tag.RELEVANT));

        assertEquals(4, processModel.getCausalEvents().size());
        assertEquals(2, processModel.getParallelGateways().size());
        assertTrue(processModel.getParallelGateways().contains(new Gateway(Gateway.GatewayType.JOIN, "C", new BranchPair("B", "D"))));
        assertTrue(processModel.getParallelGateways().contains(new Gateway(Gateway.GatewayType.SPLIT, "A", new BranchPair("B", "D"))));
        assertEquals(0, processModel.getChoiceGateways().size());
    }

}
