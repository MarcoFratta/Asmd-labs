package u01.calculator;

import io.cucumber.java.en.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorSteps {
    private Calculator calculator;
    private int counter;

    @Given("I have a Calculator")
    public void iHaveACalculator() {
        this.calculator = new Calculator();
    }

    @When("I add {int} and {int}")
    public void iAddAnd(int arg0, int arg1) {
        this.calculator.enter(arg0);
        this.calculator.enter(arg1);
    }

    @Then("the sum should be {int}")
    public void theSumShouldBe(int arg0) {
        this.calculator.add();
        if (arg0 != this.calculator.getResult()) { // or using Junit's asserts
            throw new IllegalStateException();
        }
    }
    @Then("the difference should be {int}")
    public void theDifferenceShouldBe(final int arg0) {
        this.calculator.subtract();
        final int res = this.calculator.getResult();
        assertEquals(arg0, res);
    }
}
