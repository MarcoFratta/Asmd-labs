package u01.llm.copilot;

import exam.Logger;
import exam.LogicImpl;
import exam.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

public class TestWithImpl {

    private LogicImpl logic;
    private Logger logger;
    private Position goal;

    // Copilot does not understand that there is the LoggerImpl class
        @BeforeEach
        public void setup() {
            this.logger = new Logger() {
                @Override
                public void log(final String message) {
                    // Do nothing for this test
                }

                @Override
                public void handle(final Consumer<String> handler) {
                    // Do nothing for this test
                }
            };
            this.goal = new Position(5, 5);
            this.logic = new LogicImpl(10, 10, this.logger, this.goal);
        }

        @Test
        public void testHit() {
            final Position hitPosition = new Position(3, 3);
            this.logic.hit(hitPosition);
            assertTrue(this.logic.isMarked(hitPosition));
        }

        @Test
        public void testIsOver() {
            assertFalse(this.logic.isOver());
            this.logic.hit(this.goal);
            assertTrue(this.logic.isOver());
        }


    // again, here the tests use the LoggerImpl logic to test
    @Test
    public void testTriangleCreation() {
        Position hitPosition = new Position(3, 3);
        logic.hit(hitPosition);

        // Check that all positions in the triangle to the right of the hit position are marked
        for (int x = hitPosition.x() + 1; x < logic.width(); x++) {
            for (int y = 0; y < logic.height(); y++) {
                if (Math.abs(hitPosition.y() - y) <= x - hitPosition.x()) {
                    assertTrue(logic.isMarked(new Position(x, y)));
                } else {
                    assertFalse(logic.isMarked(new Position(x, y)));
                }
            }
        }
    }
}
