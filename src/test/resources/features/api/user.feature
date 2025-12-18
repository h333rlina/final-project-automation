Feature: User API Test

  @api
  Scenario: Get list of users
    Given I set API header authentication
    When I get list of users
    Then response status code should be 200

  @api
  Scenario: Get user by ID
    Given I set API header authentication
    When I send GET request to get user with id "60d0fe4f5311236168a10a06"
    Then response status code should be 200
    And user name should not be null

  @api
  Scenario: Create user
    Given I set API header authentication
    When I create a new user with data
      | firstName | lastName | email           |
      | Herlina      | Ayu      | herlina@gmail.com|
    Then response status code should be 200
    And created user firstName should be "Herlina"
