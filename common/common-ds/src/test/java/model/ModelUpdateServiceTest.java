package model;

import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.map.result.MicroBatchRelationCountMap;
import de.cau.se.model.MinedProcessModel;
import de.cau.se.model.ModelUpdateService;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class ModelUpdateServiceTest {

    @Test
    public void testUpdate() {
        final ModelUpdateService testee = new ModelUpdateService(0.1, 0.3, new MinedProcessModel(), new MicroBatchRelationCountMap());
        testee.update(new DirectlyFollowsRelation("A", "B"));
        testee.update(new DirectlyFollowsRelation("B", "C"));
        testee.update(new DirectlyFollowsRelation("C", "D"));
        testee.update(new DirectlyFollowsRelation("A", "E"));
        testee.update(new DirectlyFollowsRelation("E", "C"));
        testee.update(new DirectlyFollowsRelation("B", "E"));
        testee.update(new DirectlyFollowsRelation("C", "F"));
        testee.update(new DirectlyFollowsRelation("A", "B"));
        MinedProcessModel processModel = testee.getProcessModel();
        assertEquals(7, processModel.getCausalEvents().size());
        assertEquals(4, processModel.getParallelGateways().size());
        assertEquals(1, processModel.getChoiceGateways().size());
    }

    @Test
    public void testUpdateWithCount() {
        final ModelUpdateService testee = new ModelUpdateService(0.1, 0.5, new MinedProcessModel(), new MicroBatchRelationCountMap());
        testee.update(new DirectlyFollowsRelation("A", "B"), 7);
        testee.update(new DirectlyFollowsRelation("B", "C"), 3);
        testee.update(new DirectlyFollowsRelation("C", "D"), 4);
        testee.update(new DirectlyFollowsRelation("A", "E"), 5);
        testee.update(new DirectlyFollowsRelation("E", "C"), 3);
        testee.update(new DirectlyFollowsRelation("B", "E"), 2);
        testee.update(new DirectlyFollowsRelation("C", "F"), 3);
        MinedProcessModel processModel = testee.getProcessModel();
        assertEquals(7, processModel.getCausalEvents().size());
        assertEquals(3, processModel.getParallelGateways().size());
        assertEquals(2, processModel.getChoiceGateways().size());
    }

    @Test
    public void testUpdateWithCountAndEmptyActivitiesToUpdate() {
        final ModelUpdateService testee = new ModelUpdateService(0.1, 0.5, new MinedProcessModel(), new MicroBatchRelationCountMap());
        testee.update(new DirectlyFollowsRelation("A", "B"), 7, new HashSet<>());
        testee.update(new DirectlyFollowsRelation("B", "C"), 3, new HashSet<>());
        testee.update(new DirectlyFollowsRelation("C", "D"), 4, new HashSet<>());
        testee.update(new DirectlyFollowsRelation("A", "E"), 5, new HashSet<>());
        testee.update(new DirectlyFollowsRelation("E", "C"), 3, new HashSet<>());
        testee.update(new DirectlyFollowsRelation("B", "E"), 2, new HashSet<>());
        testee.update(new DirectlyFollowsRelation("C", "F"), 3, new HashSet<>());
        MinedProcessModel processModel = testee.getProcessModel();
        assertEquals(7, processModel.getCausalEvents().size());
        assertEquals(0, processModel.getParallelGateways().size());
        assertEquals(0, processModel.getChoiceGateways().size());
    }

    @Test
    public void testUpdateWithCountAndActivitiesToUpdate() {
        final ModelUpdateService testee = new ModelUpdateService(0.1, 0.5, new MinedProcessModel(), new MicroBatchRelationCountMap());
        testee.update(new DirectlyFollowsRelation("A", "B"), 7, Set.of("A", "B"));
        testee.update(new DirectlyFollowsRelation("B", "C"), 3, Set.of("A", "B", "C"));
        testee.update(new DirectlyFollowsRelation("C", "D"), 4, Set.of("A", "B", "C", "D"));
        testee.update(new DirectlyFollowsRelation("A", "E"), 5, Set.of("A", "B", "C", "D", "E"));
        testee.update(new DirectlyFollowsRelation("E", "C"), 3, Set.of("A", "B", "C", "D", "E"));
        testee.update(new DirectlyFollowsRelation("B", "E"), 2, Set.of("A", "B", "C", "D", "E"));
        testee.update(new DirectlyFollowsRelation("C", "F"), 3, Set.of("A", "B", "C", "D", "E", "F"));
        MinedProcessModel processModel = testee.getProcessModel();
        assertEquals(7, processModel.getCausalEvents().size());
        assertEquals(4, processModel.getParallelGateways().size());
        assertEquals(1, processModel.getChoiceGateways().size());
    }
}
