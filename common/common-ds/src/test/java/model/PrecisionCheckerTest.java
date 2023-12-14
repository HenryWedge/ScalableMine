package model;

import de.cau.se.model.PrecisionChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class PrecisionCheckerTest {

    PrecisionChecker testee;

    @BeforeEach
    public void prepare() {
        testee = new PrecisionChecker(false, "");
    }
}
