package u01.llm.chatgpt;
import static org.junit.jupiter.api.Assertions.*;

import exam.LoggerImpl;
import exam.Logic;
import exam.LogicImpl;
import exam.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestWithImpl {

    private Logic logic;
    private final int width = 11;
    private final int height = 11;
    private final Position goalPosition = new Position(5, 5);

    @BeforeEach
    public void setUp() {
        this.logic = new LogicImpl(this.width, this.height, new LoggerImpl(), this.goalPosition);
    }

    @Test
    public void testInitialization() {
        assertEquals(this.width, this.logic.width());
        assertEquals(this.height, this.logic.height());
        assertEquals(this.goalPosition, this.logic.getGoal());
        assertFalse(this.logic.isMarked(this.goalPosition));
        assertFalse(this.logic.isOver());
    }

    // wrong way of testing
    @Test
    public void testHit() {
        final Position hitPosition = new Position(3, 3);
        this.logic.hit(hitPosition);

        assertTrue(this.logic.isMarked(hitPosition));
        // Check if the positions forming the triangle are marked correctly
        for (int x = 1; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                if (Math.abs(hitPosition.y() - y) <= x && hitPosition.x() + x < this.width) {
                    assertTrue(this.logic.isMarked(new Position(hitPosition.x() + x, y)));
                }
            }
        }
    }

    // simple but correct tests
    @Test
    public void testHitGoal() {
        final Position hitPosition = new Position(5, 5);
        this.logic.hit(hitPosition);

        assertTrue(this.logic.isMarked(hitPosition));
        assertTrue(this.logic.isOver());
    }

    @Test
    public void testMissGoal() {
        final Position hitPosition = new Position(1, 1);
        this.logic.hit(hitPosition);

        assertTrue(this.logic.isMarked(hitPosition));
        assertFalse(this.logic.isOver());
    }

    @Test
    public void testIsMarked() {
        final Position hitPosition = new Position(4, 4);
        this.logic.hit(hitPosition);

        assertTrue(this.logic.isMarked(hitPosition));
        assertFalse(this.logic.isMarked(new Position(0, 0)));
    }
}