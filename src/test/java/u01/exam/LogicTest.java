package u01.exam;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.sl.In;
import io.cucumber.messages.internal.com.google.common.collect.Multiset;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LogicTest {
    private Logic logic;

    @Given("a matrix {int}x{int}")
    public void aMatrixX(final int width, final int height) {
        this.logic = new LogicImpl(width, height, new LoggerImpl(), new Position(0, 0));
    }

    @When("i select the cell {int}, {int}")
    public void iSelectTheCell(final int x, final int y) {
        this.logic.hit(new Position(x, y));
    }


    @Then("then the following cells should be selected")
    public void thenTheFollowingCellsXAndYCellsShouldBeSelected(final List<List<Integer>> list) {
        for (final List<Integer> l : list) {
            assertTrue(this.logic.isMarked(new Position(l.get(0), l.get(1))));
        }
    }

    @Given("a matrix {int}x{int} with the target at {int}, {int}")
    public void aMatrixXWithTheTargetAt(final int width, final int height, final int x, final int y) {
        this.logic = new LogicImpl(width, height, new LoggerImpl(), new Position(x, y));
    }

    @Then("the game should be over")
    public void theGameShouldBeOver() {
        assertTrue(this.logic.isOver());
    }

    @Then("the game should NOT be over")
    public void theGameShouldNOTBeOver() {
        assertFalse(this.logic.isOver());
    }

    @Then("no cell should be selected")
    public void noCellShouldBeSelected() {
        for (int x = 0; x < this.logic.width(); x++) {
            for (int y = 0; y < this.logic.height(); y++) {
                assertFalse(this.logic.isMarked(new Position(x, y)));
            }
        }
    }
}
