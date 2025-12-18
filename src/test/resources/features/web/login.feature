@web
Feature: Login Web UI

Scenario: Successful login
  Given user open demoblaze website
  When user login with username "test" and password "test"
  Then login should be success
