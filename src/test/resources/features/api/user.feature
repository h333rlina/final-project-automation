@api
Feature: User API Automation Tests

  @smoke @positive
  Scenario: Get list of users successfully
    Given I set valid API authentication header
    When I send GET request to list users
    Then the response status code should be 200
    And the response should contain user list
    And the response should contain pagination data

  @positive
  Scenario: Get limited user list
    Given I set valid API authentication header
    When I send GET request to list users with limit 5
    Then the response status code should be 200
    And the user list should contain 5 users

  @positive
  Scenario: Get user by ID
    Given I set valid API authentication header
    When I send GET request to get first user from list
    Then the response status code should be 200
    And the user should have valid data
    And the user email should be valid format

  @positive
  Scenario: Create new user with valid data
    Given I set valid API authentication header
    When I create a new user with valid data
    Then the response status code should be 200
    And the response should contain user id
    And the user should have valid data

  @positive
  Scenario: Create user with specific data
    Given I set valid API authentication header
    When I create a new user with data
      | firstName | lastName | email           | title |
      | Herlina   | Ayu      | herlina@test.com | ms   |
    Then the response status code should be 200
    And the created user should have firstName "Herlina"

  @positive
  Scenario: Update user information
    Given I set valid API authentication header
    When I update first user's information
    Then the response status code should be 200
    And the created user should have firstName "UpdatedFirstName"

  @positive
  Scenario: Delete created user
    Given I set valid API authentication header
    And I create a new user with valid data
    When I delete the created user
    Then the response status code should be 200

  @negative
  Scenario: Get user with non-existent ID
    Given I set valid API authentication header
    When I send GET request with non-existent user id
    Then the response status code should be 404
    And the response should contain error message

  @negative
  Scenario: Get user with invalid ID format
    Given I set valid API authentication header
    When I send GET request with invalid user id format
    Then the response status code should be 400
    And the response should contain error message

  @negative
  Scenario: Create user with duplicate email
    Given I set valid API authentication header
    When I create a user with duplicate email
    Then the response status code should be 400
    And the response should contain error message

  @negative
  Scenario: Create user with invalid email format
    Given I set valid API authentication header
    When I create a user with invalid email format
    Then the response status code should be 400
    And the response should contain error message

  @negative
  Scenario: Create user with missing required fields
    Given I set valid API authentication header
    When I create a user with missing required fields
    Then the response status code should be 400
    And the response should contain error message

  @negative
  Scenario: Delete non-existent user
    Given I set valid API authentication header
    When I delete non-existent user
    Then the response status code should be 404
    And the response should contain error message

  @negative
  Scenario: Access API without authentication
    Given I set API header without authentication
    When I send GET request to list users
    Then the response status code should be 403
    And the response should contain error about missing app-id

  @negative
  Scenario: Access API with invalid authentication
    Given I set invalid API authentication header
    When I send GET request to list users
    Then the response status code should be 403
    And the response should contain error about invalid app-id