package api.stepdefinitions.web;

import io.cucumber.java.en.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class WebSteps {

    WebDriver driver;
    WebDriverWait wait;

    @Given("user open demoblaze website")
    public void openWebsite() {
        io.github.bonigarcia.wdm.WebDriverManager.chromedriver().setup();
        org.openqa.selenium.chrome.ChromeOptions options = new org.openqa.selenium.chrome.ChromeOptions();

        if (System.getenv("GITHUB_ACTIONS") != null) {
            options.addArguments("--headless");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        } else {
            options.setExperimentalOption("detach", true);
        }

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(90));
        driver.get("https://www.demoblaze.com/");
    }


    @When("user login with username {string} and password {string}")
    public void loginUser(String username, String password) {

        driver.findElement(By.id("login2")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginusername")));
        driver.findElement(By.id("loginusername")).sendKeys(username);
        driver.findElement(By.id("loginpassword")).sendKeys(password);

        driver.findElement(By.xpath("//button[text()='Log in']")).click();
    }

    @Then("login should be success")
    public void validateLogin() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nameofuser")));

        String welcome = driver.findElement(By.id("nameofuser")).getText();
        assertTrue(welcome.contains("Welcome"), "User was not logged in!");

        if (System.getenv("GITHUB_ACTIONS") != null) {
            driver.quit();
        }
    }

}
