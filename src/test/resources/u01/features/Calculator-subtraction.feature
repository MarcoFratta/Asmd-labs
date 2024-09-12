Feature:  Subtracting numbers with a Calculator
  In order to not learn math
  As someone who is bad at math
  I want to be able to subtract numbers using a Calculator

  Background: Start with a Calculator
    Given I have a Calculator


  Scenario: Subtract two positive numbers
    When I add 5 and 3
    Then the difference should be 2


  Scenario:  Subtract a positive and negative number
    When I add 3 and -5
    Then the difference should be 8

  Scenario:  Subtract the same number
    When I add 3 and 3
    Then the difference should be 0

