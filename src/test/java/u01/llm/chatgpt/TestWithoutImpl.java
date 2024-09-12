package u01.llm.chatgpt;

import u01.exam.LoggerImpl;
import u01.exam.LogicImpl;
import u01.exam.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import u01.exam.Logic;
import static org.junit.jupiter.api.Assertions.*;

class TestWithoutImpl {

    private Logic logic;
    private final int width = 11;
    private final int height = 11;
    private final Position goal = new Position(5, 5);

    @BeforeEach
    public void setUp() {
        this.logic = new LogicImpl(this.width, this.height, new LoggerImpl(), this.goal);
    }

        @Test
        public void testInitialSetup() {
            assertEquals(this.width, this.logic.width());
            assertEquals(this.height, this.logic.height());
            assertEquals(this.goal, this.logic.getGoal());
            assertFalse(this.logic.isOver());
        }

        // wrong test, does not understand game logic
        @Test
        public void testHitMiss() {
            final Position hitPosition = new Position(3, 3);
            this.logic.hit(hitPosition);
            assertFalse(this.logic.isOver());
        }

        @Test
        public void testHitTarget() {
            this.logic.hit(this.goal);
            assertTrue(this.logic.isOver());
        }

        @Test
        public void testIsMarked() {
            final Position hitPosition = new Position(3, 3);
            this.logic.hit(hitPosition);
            assertTrue(this.logic.isMarked(hitPosition));
            assertFalse(this.logic.isMarked(new Position(0, 0)));
        }

        @Test
        public void testGameEnd() {
            final Position hitPosition = new Position(5, 5);
            this.logic.hit(hitPosition);
            assertTrue(this.logic.isOver());
        }

        // wrong test, does not understand game logic
        @Test
        public void testMultipleHits() {
            final Position missPosition = new Position(3, 3);
            final Position hitPosition = new Position(5, 5);
            this.logic.hit(missPosition);
            assertFalse(this.logic.isOver());
            this.logic.hit(hitPosition);
            assertTrue(this.logic.isOver());
        }
    }