
Feature: As a User i want to select a cell in a matrix
    in order to create triangles
    until a point of the triangles hit a specific target


  Scenario: At the start of the game no cell is selected
    Given a matrix 10x10 with the target at 6, 6
    Then no cell should be selected

  Scenario: At the start of the game it is not over
    Given a matrix 10x10 with the target at 6, 6
    Then the game should NOT be over

  Scenario: Selecting a cell in a matrix should create a triangle
    to the right until it hits a border
    Given a matrix 5x5
    When i select the cell 1, 3
    Then then the following cells should be selected
      | 1 | 3 |
      | 2 | 2 |
      | 2 | 2 |
      | 2 | 4 |

  Scenario: Selecting a cell causes game over
    Given a matrix 10x10 with the target at 6, 6
    When i select the cell 4, 4
    Then the game should be over

  Scenario: Selecting a cell does not causes game over
    Given a matrix 10x10 with the target at 4, 3
    When i select the cell 5, 3
    Then the game should NOT be over
