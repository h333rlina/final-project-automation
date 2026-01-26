package api.stepdefinitions.api;

import io.cucumber.java.en.*;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserSteps {

    Response response;
    RequestSpecification request;
    String createdUserId;
    String authToken = "63a804408eb0cb069b57e43a";

    @Given("I set valid API authentication header")
    public void setValidHeader() {
        request = given()
                .baseUri("https://dummyapi.io/data/v1")
                .header("app-id", authToken)
                .contentType("application/json")
                .log().all();
    }

    @Given("I set invalid API authentication header")
    public void setInvalidHeader() {
        request = given()
                .baseUri("https://dummyapi.io/data/v1")
                .header("app-id", "invalid-app-id-123")
                .contentType("application/json")
                .log().all();
    }

    @Given("I set API header without authentication")
    public void setHeaderWithoutAuth() {
        request = given()
                .baseUri("https://dummyapi.io/data/v1")
                .contentType("application/json")
                .log().all();
    }

    @When("I send GET request to list users")
    public void getListOfUsers() {
        response = request.get("/user");
        response.then().log().all();
    }

    @When("I send GET request to list users with limit {int}")
    public void getListOfUsersWithLimit(int limit) {
        response = request
                .param("limit", limit)
                .get("/user");
        response.then().log().all();
    }

    @When("I send GET request to list users with page {int} and limit {int}")
    public void getListOfUsersWithPagination(int page, int limit) {
        response = request
                .param("page", page)
                .param("limit", limit)
                .get("/user");
        response.then().log().all();
    }

    @Then("the response should contain user list")
    public void verifyUserList() {
        response.then()
                .body("data", not(empty()))
                .body("total", greaterThan(0));
    }

    @Then("the user list should contain {int} users")
    public void verifyUserListSize(int expectedCount) {
        response.then()
                .body("data.size()", equalTo(expectedCount))
                .body("limit", equalTo(expectedCount));
    }

    @When("I send GET request to get first user from list")
    public void getFirstUserFromList() {
        Response listResponse = request.get("/user");
        listResponse.then().statusCode(200);

        String firstUserId = listResponse.jsonPath().getString("data[0].id");
        System.out.println("First user ID: " + firstUserId);

        response = request.get("/user/" + firstUserId);
        response.then().log().all();
    }

    @When("I send GET request to get user with id {string}")
    public void getUserById(String userId) {
        response = request.get("/user/" + userId);
        response.then().log().all();
    }

    @When("I send GET request with non-existent user id")
    public void getNonExistentUser() {
        String nonExistentId = "60d0fe4f5311236168a10a99";
        response = request.get("/user/" + nonExistentId);
        response.then().log().all();
    }

    @When("I send GET request with invalid user id format")
    public void getInvalidFormatUserId() {
        response = request.get("/user/invalid-id-format");
        response.then().log().all();
    }

    @When("I create a new user with valid data")
    public void createUserWithValidData() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uniqueEmail = "testuser" + timestamp + "@example.com";

        Map<String, Object> userData = new HashMap<>();
        userData.put("firstName", "Test");
        userData.put("lastName", "User" + timestamp);
        userData.put("email", uniqueEmail);
        userData.put("title", "mr");

        response = request.body(userData).post("/user/create");
        response.then().log().all();

        if (response.getStatusCode() == 200) {
            createdUserId = response.jsonPath().getString("id");
        }
    }

    @When("I create a new user with data")
    public void createUserWithData(List<Map<String, String>> data) {
        Map<String, String> body = new HashMap<>(data.get(0));

        String originalEmail = body.get("email");
        if (originalEmail != null && originalEmail.contains("@")) {
            String uniqueEmail = originalEmail.split("@")[0] +
                    System.currentTimeMillis() +
                    "@" + originalEmail.split("@")[1];
            body.put("email", uniqueEmail);
        }

        response = request.body(body).post("/user/create");
        response.then().log().all();

        if (response.getStatusCode() == 200) {
            createdUserId = response.jsonPath().getString("id");
        }
    }

    @When("I create a user with duplicate email")
    public void createUserWithDuplicateEmail() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String email = "duplicate" + timestamp + "@example.com";

        Map<String, Object> firstUser = new HashMap<>();
        firstUser.put("firstName", "First");
        firstUser.put("lastName", "User");
        firstUser.put("email", email);
        firstUser.put("title", "mr");

        Response firstResponse = request.body(firstUser).post("/user/create");
        String firstUserId = firstResponse.jsonPath().getString("id");

        Map<String, Object> secondUser = new HashMap<>();
        secondUser.put("firstName", "Second");
        secondUser.put("lastName", "User");
        secondUser.put("email", email);
        secondUser.put("title", "mr");

        response = request.body(secondUser).post("/user/create");
        response.then().log().all();

        if (firstUserId != null) {
            request.delete("/user/" + firstUserId);
        }
    }

    @When("I create a user with invalid email format")
    public void createUserWithInvalidEmail() {
        Map<String, Object> invalidUser = new HashMap<>();
        invalidUser.put("firstName", "Invalid");
        invalidUser.put("lastName", "Email");
        invalidUser.put("email", "not-an-email");
        invalidUser.put("title", "mr");

        response = request.body(invalidUser).post("/user/create");
        response.then().log().all();
    }

    @When("I create a user with missing required fields")
    public void createUserWithMissingFields() {
        Map<String, Object> incompleteUser = new HashMap<>();
        incompleteUser.put("firstName", "Incomplete");

        response = request.body(incompleteUser).post("/user/create");
        response.then().log().all();
    }

    @When("I update user {string} with new data")
    public void updateUser(String userId, List<Map<String, String>> data) {
        Map<String, String> body = new HashMap<>(data.get(0));
        response = request.body(body).put("/user/" + userId);
        response.then().log().all();
    }

    @When("I update first user's information")
    public void updateFirstUser() {
        Response listResponse = request.get("/user");
        String firstUserId = listResponse.jsonPath().getString("data[0].id");

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("firstName", "UpdatedFirstName");
        updateData.put("lastName", "UpdatedLastName");

        response = request.body(updateData).put("/user/" + firstUserId);
        response.then().log().all();
    }

    @When("I delete user with id {string}")
    public void deleteUser(String userId) {
        response = request.delete("/user/" + userId);
        response.then().log().all();
    }

    @When("I delete the created user")
    public void deleteCreatedUser() {
        if (createdUserId != null) {
            response = request.delete("/user/" + createdUserId);
            response.then().log().all();
        } else {
            throw new RuntimeException("No user was created to delete");
        }
    }

    @When("I delete non-existent user")
    public void deleteNonExistentUser() {
        response = request.delete("/user/60d0fe4f5311236168a10a99");
        response.then().log().all();
    }

    @Then("the response status code should be {int}")
    public void validateStatusCode(int statusCode) {
        response.then().statusCode(statusCode);
    }

    @Then("the user should have valid data")
    public void verifyUserHasValidData() {
        response.then()
                .body("id", notNullValue())
                .body("firstName", notNullValue())
                .body("lastName", notNullValue())
                .body("email", notNullValue())
                .body("registerDate", notNullValue())
                .body("updatedDate", notNullValue());
    }

    @Then("the created user should have firstName {string}")
    public void verifyCreatedUserName(String firstName) {
        response.then().body("firstName", equalTo(firstName));
    }

    @Then("the response should contain error message")
    public void verifyErrorMessage() {
        response.then().body("error", notNullValue());
    }

    @Then("the response should contain pagination data")
    public void verifyPaginationData() {
        response.then()
                .body("page", notNullValue())
                .body("limit", notNullValue())
                .body("total", notNullValue());
    }

    @Then("the user email should be valid format")
    public void verifyEmailFormat() {
        response.then().body("email", matchesRegex("^[A-Za-z0-9+_.-]+@(.+)$"));
    }

    @Then("the response should contain data field")
    public void verifyDataField() {
        response.then().body("data", notNullValue());
    }

    @Then("the response should contain user id")
    public void verifyUserIdExists() {
        response.then().body("id", notNullValue());
    }

    @Then("the response should contain error about invalid app-id")
    public void verifyInvalidAppIdError() {
        response.then()
                .body("error", equalTo("APP_ID_NOT_EXIST"))
                .body("data", nullValue());
    }

    @Then("the response should contain error about missing app-id")
    public void verifyMissingAppIdError() {
        response.then()
                .body("error", equalTo("APP_ID_MISSING"))
                .body("data", nullValue());
    }
}