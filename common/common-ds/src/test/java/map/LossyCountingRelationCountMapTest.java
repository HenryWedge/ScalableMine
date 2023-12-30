package map;

import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.map.result.CountBasedRelationCountMap;
import de.cau.se.map.result.LossyCountingRelationCountMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LossyCountingRelationCountMapTest {

    @Test
    void testRelevantEventsPresent() {
        final LossyCountingRelationCountMap<DirectlyFollowsRelation> testee = new LossyCountingRelationCountMap<>();
        testee.insertOrUpdate(new DirectlyFollowsRelation("A", "B"), 1);
        testee.insertOrUpdate(new DirectlyFollowsRelation("A", "B"), 1);
        testee.removeIrrelevant(1);
        assertTrue(testee.containsKey(new DirectlyFollowsRelation("A", "B")));
        testee.insertOrUpdate(new DirectlyFollowsRelation("B", "C"), 1);
        testee.insertOrUpdate(new DirectlyFollowsRelation("A", "C"), 1);
        testee.removeIrrelevant(2);
        assertTrue(testee.containsKey(new DirectlyFollowsRelation("A", "B")));
        assertTrue(testee.containsKey(new DirectlyFollowsRelation("B", "C")));
        assertTrue(testee.containsKey(new DirectlyFollowsRelation("A", "C")));
        testee.insertOrUpdate(new DirectlyFollowsRelation("A", "B"), 1);
        testee.insertOrUpdate(new DirectlyFollowsRelation("A", "E"), 2);
        testee.removeIrrelevant(3);
        assertTrue(testee.containsKey(new DirectlyFollowsRelation("A", "B")));
        assertTrue(testee.containsKey(new DirectlyFollowsRelation("A", "E")));
        assertFalse(testee.containsKey(new DirectlyFollowsRelation("B", "C")));
        assertFalse(testee.containsKey(new DirectlyFollowsRelation("A", "C")));
    }

}
