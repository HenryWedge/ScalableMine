package map;

import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.map.result.CountBasedRelationCountMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CountBasedRelationCountMapTest {
    @Test
    void testRelevantEventsPresent() {
        final CountBasedRelationCountMap<DirectlyFollowsRelation> testee = new CountBasedRelationCountMap<>();
        testee.insertOrUpdate(new DirectlyFollowsRelation("A", "B"), 1);
        testee.insertOrUpdate(new DirectlyFollowsRelation("A", "B"), 2);
        assertEquals(new DirectlyFollowsRelation("A", "B"),
                testee.getRelevantRelations(2).stream().findFirst().get());
        assertTrue(testee.getIrrelevantRelations(1).isEmpty());
    }

    @Test
    void testRelevantAndIrrelevantEventsPresent() {
        final CountBasedRelationCountMap<DirectlyFollowsRelation> testee = new CountBasedRelationCountMap<>();
        testee.insertOrUpdate(new DirectlyFollowsRelation("A", "B"), 1);
        testee.insertOrUpdate(new DirectlyFollowsRelation("A", "B"), 2);
        testee.insertOrUpdate(new DirectlyFollowsRelation("A", "C"), 1);
        assertEquals(new DirectlyFollowsRelation("A", "B"),
                testee.getRelevantRelations(2).stream().findFirst().get());
        assertEquals(new DirectlyFollowsRelation("A", "C"),
                testee.getIrrelevantRelations(1).stream().findFirst().get());
    }

    @Test
    void testIrrelevantEventsPresent() {
        final CountBasedRelationCountMap<DirectlyFollowsRelation> testee = new CountBasedRelationCountMap<>();
        testee.insertOrUpdate(new DirectlyFollowsRelation("A", "B"), 1);
        testee.insertOrUpdate(new DirectlyFollowsRelation("A", "B"), 1);
        testee.insertOrUpdate(new DirectlyFollowsRelation("A", "C"), 1);
        assertTrue(testee.getRelevantRelations(2).isEmpty());
        assertEquals(new DirectlyFollowsRelation("A", "C"),
                testee.getIrrelevantRelations(1).stream().findFirst().get());
    }
}
