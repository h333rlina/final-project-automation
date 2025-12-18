package api.stepdefinitions.api;

import io.cucumber.java.en.*;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserSteps {

    Response response;
    RequestSpecification request;
    String firstUserId;

    @Given("I set API header authentication")
    public void setHeader() {
        request = given()
                .baseUri("https://dummyapi.io/data/v1")
                .header("app-id", "63a804408eb0cb069b57e43a")
                .contentType("application/json")
                .log().all();
    }

    @When("I send GET request to get first user from list")
    public void getFirstUserFromList() {
        Response listResponse = request.get("/user");
        listResponse.then().statusCode(200);

        firstUserId = listResponse.jsonPath().getString("data[0].id");

        response = request.get("/user/" + firstUserId);
        response.then().statusCode(200).log().all();
    }


    @When("I get list of users")
    public void getListOfUsers() {
        response = request.get("/user");
        response.then().log().all()
                .statusCode(200);
    }

    @When("I send GET request to get user with id {string}")
    public void getUserById(String userId) {
        response = request.get("/user/" + userId);
        response.then().log().all();
    }


    @Then("user name should not be null")
    public void userNameNotNull() {
        response.then().body("firstName", notNullValue());
    }

    @When("I create a new user with data")
    public void createUser(List<Map<String, String>> data) {
        Map<String, String> body = new HashMap<>(data.get(0));

        String uniqueEmail = body.get("email").replace("@", System.currentTimeMillis() + "@");
        body.put("email", uniqueEmail);

        response = request.body(body).post("/user/create");
        response.then().log().all().statusCode(200);
    }

    @Then("response status code should be {int}")
    public void validateStatus(int statusCode) {
        response.then().statusCode(statusCode);
    }

    @Then("user firstName should not be null")
    public void validateUserName() {
        response.then().body("firstName", notNullValue());
    }

    @Then("created user firstName should be {string}")
    public void validateCreatedUserName(String firstName) {
        response.then().body("firstName", equalTo(firstName));
    }
}
