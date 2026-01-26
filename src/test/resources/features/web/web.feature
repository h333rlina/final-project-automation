@web
Feature: Web Automation Tests for Demoblaze

  @smoke @positive
  Scenario: Verify successful login
    Given user opens Demoblaze website
    When user logs in with valid credentials
    Then login button should not be visible
    And logout button should be visible in topbar

  @positive
  Scenario: Browse products by category
    Given user opens Demoblaze website
    And user logs in with valid credentials
    When user goes to Phones category
    Then products from Phones category should be displayed
    And at least 5 products should be visible

  @positive
  Scenario: Add product to cart
    Given user opens Demoblaze website
    And user logs in with valid credentials
    When user adds Samsung galaxy s6 to cart
    And user opens cart from topbar menu
    Then Samsung galaxy s6 should be in cart

  @positive
    Scenario: Remove product from cart
      Given user opens Demoblaze website
      And user logs in with valid credentials
      And user adds Samsung galaxy s6 to cart
      When user opens cart from topbar menu
      And user deletes Samsung galaxy s6 from cart
      Then cart should be empty

  @positive
  Scenario: Complete checkout flow
    Given user opens Demoblaze website
    And user logs in with valid credentials
    When user adds Samsung galaxy s6 to cart
    And user opens cart from topbar menu
    And user clicks Place Order button in cart
    And user fills order form with details
    And user completes purchase by clicking Purchase button
    Then purchase confirmation should be displayed
    When user clicks OK on purchase confirmation
    Then cart should be empty

  @negative
  Scenario: Login with invalid credentials
    Given user opens Demoblaze website
    When user tries to login with wrong credentials
    Then login should fail with alert

  @negative
  Scenario: Checkout with empty cart
    Given user opens Demoblaze website
    And user logs in with valid credentials
    When user opens cart from topbar menu
    And user clicks Place Order button in cart
    Then error should be shown for empty cart

  @negative
  Scenario: Try purchase without filling required fields
    Given user opens Demoblaze website
    And user logs in with valid credentials
    And user adds Samsung galaxy s6 to cart
    And user opens cart from topbar menu
    When user clicks Place Order button in cart
    And user submits empty order form
    Then validation errors should be shown

  @e2e @complete
  Scenario: Complete E2E flow with order cancellation
    Given user opens Demoblaze website
    And user logs in with valid credentials
    When user goes to Phones category
    And user adds Samsung galaxy s6 to cart
    And user opens cart from topbar menu
    Then Samsung galaxy s6 should be in cart
    When user clicks Place Order button in cart
    And user fills order form with details
    And user cancels purchase and returns to cart
    Then Samsung galaxy s6 should still be in cart
    When user logs out from topbar menu
    Then login button should be visible in topbar
    And close browser after test completion