package model;

import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.map.result.CountBasedRelationCountMap;
import de.cau.se.model.MinedProcessModel;
import de.cau.se.model.PrecisionChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrecisionCheckerTest {

    PrecisionChecker testee;

    @BeforeEach
    public void prepare() {
        testee = new PrecisionChecker(false, "");
    }

    @Test
    void testJaccardCoefficientSimilarity() {
        final MinedProcessModel processModel = new MinedProcessModel();
        final MinedProcessModel processModel2 = new MinedProcessModel();
        processModel.getCausalEvents().add(new DirectlyFollowsRelation("A", "B"));
        processModel2.getCausalEvents().add(new DirectlyFollowsRelation("A", "B"));
        assertEquals(1.0d, testee.calculateModelToModelSimilarityWithJaccardScore(
                processModel.getCausalEvents(),
                processModel2.getCausalEvents()));
    }

    @Test
    void testJaccardCoefficientDissimilarity() {
        final MinedProcessModel processModel = new MinedProcessModel();
        final MinedProcessModel processModel2 = new MinedProcessModel();
        processModel.getCausalEvents().add(new DirectlyFollowsRelation("A", "B"));
        processModel2.getCausalEvents().add(new DirectlyFollowsRelation("B", "C"));
        assertEquals(0.0d, testee.calculateModelToModelSimilarityWithJaccardScore(
                processModel.getCausalEvents(),
                processModel2.getCausalEvents()));
    }

    @Test
    void testJaccardCoefficientOneSimilarElement() {
        final MinedProcessModel processModel = new MinedProcessModel();
        final MinedProcessModel processModel2 = new MinedProcessModel();
        processModel.getCausalEvents().add(new DirectlyFollowsRelation("A", "B"));
        processModel.getCausalEvents().add(new DirectlyFollowsRelation("C", "D"));
        processModel2.getCausalEvents().add(new DirectlyFollowsRelation("A", "B"));
        processModel2.getCausalEvents().add(new DirectlyFollowsRelation("B", "C"));
        assertTrue(testee.calculateModelToModelSimilarityWithJaccardScore(
                processModel.getCausalEvents(),
                processModel2.getCausalEvents()) - 0.333 < 0.01);
    }
}
