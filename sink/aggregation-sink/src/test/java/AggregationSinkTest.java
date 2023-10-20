import de.cau.se.datastructure.BranchPair;
import de.cau.se.datastructure.DirectlyFollows;
import de.cau.se.datastructure.Gateway;
import de.cau.se.datastructure.Result;
import de.cau.se.map.ResultMap;
import de.cau.se.model.EventRelationLogger;
import de.cau.se.model.PrecisionChecker;
import de.cau.se.AggregationSink;
import de.cau.se.model.ModelUpdater;
import org.apache.kafka.clients.consumer.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.openMocks;

// TODO here fix tests
public class AggregationSinkTest {

    @Mock
    private Consumer<String, Result> consumer;

    private Set<DirectlyFollows> causalDependencyMap;
    private Set<Gateway> xorDependencyMap;
    private Set<Gateway> parallelDependencyMap;
    private ResultMap resultMap;
    private AggregationSink testee;
    private ModelUpdater modelUpdater;
    private EventRelationLogger eventRelationLogger;
    private PrecisionChecker precisionChecker;

    @BeforeEach
    void prepare() {
        openMocks(this);
        causalDependencyMap = new HashSet<>();
        xorDependencyMap = new HashSet<>();
        parallelDependencyMap = new HashSet<>();
        resultMap = new ResultMap();
        modelUpdater = new ModelUpdater();
        eventRelationLogger = new EventRelationLogger();
        precisionChecker = new PrecisionChecker();

        testee = new AggregationSink(
                consumer,
                0.5,
                0.8,
                causalDependencyMap,
                parallelDependencyMap,
                xorDependencyMap,
                resultMap,
                modelUpdater,
                eventRelationLogger,
                precisionChecker);
    }

    @AfterEach
    void tearDown() {
        causalDependencyMap = null;
        xorDependencyMap = null;
        parallelDependencyMap = null;
        resultMap = null;
        testee = null;
    }

    @Test
    public void testXor() {
        testee.receive(new Result(new DirectlyFollows("A", "B"), 5));
        testee.receive(new Result(new DirectlyFollows("B", "C"), 5));
        testee.receive(new Result(new DirectlyFollows("A", "D"), 7));
        testee.receive(new Result(new DirectlyFollows("D", "C"), 6));
        assertEquals(4, causalDependencyMap.size());
        assertTrue(xorDependencyMap.contains(new Gateway(Gateway.GatewayType.SPLIT, "A",new BranchPair( "B", "D"))));
        assertTrue(xorDependencyMap.contains(new Gateway(Gateway.GatewayType.JOIN, "C", new BranchPair("B", "D"))));

        assertEquals(2, xorDependencyMap.size());
        assertEquals(0, parallelDependencyMap.size());
    }

    @Test
    public void testParallel() {
        testee.receive(new Result(new DirectlyFollows("A", "B"), 5));
        testee.receive(new Result(new DirectlyFollows("B", "D"), 3));
        testee.receive(new Result(new DirectlyFollows("D", "B"), 3));
        testee.receive(new Result(new DirectlyFollows("B", "C"), 5));
        testee.receive(new Result(new DirectlyFollows("A", "D"), 5));
        testee.receive(new Result(new DirectlyFollows("D", "C"), 5));

        assertEquals(4, causalDependencyMap.size());
        assertEquals(2, parallelDependencyMap.size());
        assertTrue(parallelDependencyMap.contains(new Gateway(Gateway.GatewayType.JOIN, "C", new BranchPair("B", "D"))));
        assertTrue(parallelDependencyMap.contains(new Gateway(Gateway.GatewayType.SPLIT, "A",new BranchPair("B", "D"))));
        assertEquals(0, xorDependencyMap.size());
    }

}
