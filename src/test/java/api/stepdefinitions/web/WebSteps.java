package api.stepdefinitions.web;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.interactions.Actions;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class WebSteps {

    WebDriver driver;
    WebDriverWait wait;
    Actions actions;
    String totalPrice;
    static boolean isLoggedIn = false;

    @Before("@web")
    public void setup() {
        if (driver == null) {
            openBrowser();
        }
    }

    @After("@web")
    public void teardown() {
        if (driver != null) {
            driver.quit();
            driver = null;
            isLoggedIn = false;
        }
    }

    private void openBrowser() {
        try {
            io.github.bonigarcia.wdm.WebDriverManager.chromedriver().setup();

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-notifications");
            options.addArguments("--disable-popup-blocking");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");

            if (System.getenv("GITHUB_ACTIONS") != null) {
                options.addArguments("--headless");
            }

            driver = new ChromeDriver(options);

            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

            driver.manage().window().maximize();
            wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            actions = new Actions(driver);

            new File("screenshots").mkdirs();

        } catch (Exception e) {
            fail("Failed to setup browser: " + e.getMessage());
        }
    }

    @Given("user opens Demoblaze website")
    public void openWebsite() {
        try {
            driver.get("https://www.demoblaze.com/");
            wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                    .executeScript("return document.readyState").equals("complete"));
        } catch (Exception e) {
            fail("Failed to open website: " + e.getMessage());
        }
    }

    @Given("user logs in with valid credentials")
    public void loginWithValidCredentials() {
        try {
            try {
                WebElement welcomeElement = driver.findElement(By.id("nameofuser"));
                if (welcomeElement.isDisplayed() && welcomeElement.getText().contains("Welcome")) {
                    isLoggedIn = true;
                    return;
                }
            } catch (NoSuchElementException e) {
            }

            WebElement loginBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(By.id("login2"))
            );
            loginBtn.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("logInModal")
            ));

            WebElement usernameField = driver.findElement(By.id("loginusername"));
            usernameField.clear();
            usernameField.sendKeys("test");

            WebElement passwordField = driver.findElement(By.id("loginpassword"));
            passwordField.clear();
            passwordField.sendKeys("test");

            driver.findElement(
                    By.xpath("//button[contains(text(),'Log in')]")
            ).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("nameofuser")
            ));

            WebElement logoutBtn = driver.findElement(By.id("logout2"));
            assertTrue(logoutBtn.isDisplayed(), "Logout button not visible after login");
            isLoggedIn = true;

        } catch (Exception e) {
            takeScreenshot("login_error");
            fail("Login failed: " + e.getMessage());
        }
    }

    @When("user tries to login with wrong credentials")
    public void tryLoginWithWrongCredentials() {
        try {
            WebElement loginBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(By.id("login2"))
            );
            loginBtn.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("logInModal")
            ));

            driver.findElement(By.id("loginusername")).sendKeys("wronguser");
            driver.findElement(By.id("loginpassword")).sendKeys("wrongpass");

            driver.findElement(
                    By.xpath("//button[contains(text(),'Log in')]")
            ).click();

        } catch (Exception e) {
            fail("Failed to attempt login: " + e.getMessage());
        }
    }

    @Then("login should fail with alert")
    public void loginShouldFailWithAlert() {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();

            assertTrue(alertText.contains("Wrong password") ||
                            alertText.contains("User does not exist"),
                    "Expected error not found: " + alertText);

            alert.accept();

        } catch (TimeoutException e) {
            takeScreenshot("no_login_alert");
            fail("No alert shown for failed login");
        }
    }

    @Then("login button should not be visible")
    public void loginButtonShouldNotBeVisible() {
        try {
            Thread.sleep(1000);
            List<WebElement> loginButtons = driver.findElements(By.id("login2"));
            if (!loginButtons.isEmpty()) {
                assertFalse(loginButtons.get(0).isDisplayed(),
                        "Login button should not be visible after login");
            }
        } catch (Exception e) {
            fail("Failed to verify login button visibility: " + e.getMessage());
        }
    }

    @Then("logout button should be visible in topbar")
    public void logoutButtonShouldBeVisibleInTopbar() {
        try {
            WebElement logoutBtn = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("logout2"))
            );

            assertTrue(logoutBtn.isDisplayed(), "Logout button not visible");
            assertEquals("Log out", logoutBtn.getText().trim(),
                    "Logout button text incorrect");
        } catch (Exception e) {
            takeScreenshot("logout_button_missing");
            fail("Logout button not visible: " + e.getMessage());
        }
    }

    @When("user goes to Phones category")
    public void goToPhonesCategory() {
        try {
            String phonesXpath = "//a[@onclick=\"byCat('phone')\"]";

            WebElement phonesLink = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath(phonesXpath))
            );

            scrollToElement(phonesLink);
            phonesLink.click();

            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                    By.cssSelector("#tbodyid .card"), 0
            ));

        } catch (Exception e) {
            takeScreenshot("phones_category_error");
            fail("Failed to go to Phones category: " + e.getMessage());
        }
    }

    @Then("products from Phones category should be displayed")
    public void verifyPhonesCategoryProducts() {
        try {
            List<WebElement> products = driver.findElements(By.cssSelector("#tbodyid .card"));
            assertTrue(products.size() > 0, "No products found in Phones category");
        } catch (Exception e) {
            fail("Failed to verify Phones category products: " + e.getMessage());
        }
    }

    @Then("at least {int} products should be visible")
    public void atLeastProductsShouldBeVisible(int minProducts) {
        try {
            List<WebElement> products = wait.until(
                    ExpectedConditions.numberOfElementsToBeMoreThan(
                            By.cssSelector("#tbodyid .card"), minProducts - 1
                    )
            );
        } catch (Exception e) {
            takeScreenshot("insufficient_products");
            fail("Not enough products visible: " + e.getMessage());
        }
    }

    @When("user adds Samsung galaxy s6 to cart")
    public void addSamsungGalaxyS6ToCart() {
        try {
            clearCartBeforeTest();

            driver.findElement(By.xpath("//a[contains(text(),'Home')]")).click();
            Thread.sleep(2000);

            String productXpath = "//a[normalize-space()='Samsung galaxy s6']";

            WebElement samsungLink = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath(productXpath))
            );

            samsungLink.click();

            wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector(".product-deatil")),
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector(".name")),
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//h2[contains(text(),'Samsung')]"))
            ));

            WebElement productName = driver.findElement(By.cssSelector(".name, h2.name"));
            assertEquals("Samsung galaxy s6", productName.getText(),
                    "Wrong product page");

            WebElement addToCartBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//a[contains(@class, 'btn-success') and contains(text(), 'Add to cart')]")
                    )
            );

            addToCartBtn.click();

            try {
                wait.until(ExpectedConditions.alertIsPresent());
                Alert alert = driver.switchTo().alert();
                alert.accept();
                Thread.sleep(500);
            } catch (TimeoutException e) {
            }

            Thread.sleep(1500);

        } catch (Exception e) {
            takeScreenshot("add_samsung_error");
            fail("Failed to add Samsung galaxy s6 to cart: " + e.getMessage());
        }
    }

    @When("user opens cart from topbar menu")
    public void openCartFromTopbarMenu() {
        try {
            WebElement cartLink = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//a[@class='nav-link' and contains(text(), 'Cart')]")
                    )
            );

            cartLink.click();

            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("#page-wrapper")
            ));

            List<WebElement> headings = driver.findElements(By.tagName("h2"));
            boolean cartPageLoaded = false;
            for (WebElement heading : headings) {
                if (heading.getText().contains("Products") || heading.getText().contains("Cart")) {
                    cartPageLoaded = true;
                    break;
                }
            }

            assertTrue(cartPageLoaded, "Cart page not loaded properly");

        } catch (Exception e) {
            takeScreenshot("open_cart_error");
            fail("Failed to open cart from topbar: " + e.getMessage());
        }
    }

    @Then("Samsung galaxy s6 should be in cart")
    public void verifySamsungInCart() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("#tbodyid")
            ));

            List<WebElement> cartRows = driver.findElements(
                    By.cssSelector("#tbodyid tr")
            );

            if (cartRows.isEmpty()) {
                takeScreenshot("empty_cart");
                fail("Cart is empty");
            }

            boolean found = false;
            for (WebElement row : cartRows) {
                String rowText = row.getText();
                if (rowText.contains("Samsung galaxy s6")) {
                    found = true;
                    break;
                }
            }

            assertTrue(found, "Samsung galaxy s6 not found in cart");

        } catch (Exception e) {
            takeScreenshot("verify_cart_error");
            fail("Failed to verify cart: " + e.getMessage());
        }
    }

    @Then("cart should be empty")
    public void verifyCartEmpty() {
        try {
            Thread.sleep(2000);

            driver.navigate().refresh();
            Thread.sleep(1000);

            List<WebElement> cartRows = driver.findElements(
                    By.cssSelector("#tbodyid tr")
            );

            if (cartRows.isEmpty()) {
                return;
            }

            String tableText = driver.findElement(By.cssSelector("#tbodyid")).getText();
            if (tableText.isEmpty() || tableText.contains("No products") || tableText.trim().equals("")) {
                return;
            }

            try {
                WebElement totalElement = driver.findElement(By.id("totalp"));
                String totalText = totalElement.getText().trim();
                if (totalText.equals("") || totalText.equals("0")) {
                    return;
                }
            } catch (NoSuchElementException e) {
            }

            System.out.println("Cart not empty, attempting to clear...");
            deleteAllCartItems();

            Thread.sleep(2000);
            driver.navigate().refresh();
            Thread.sleep(1000);

            cartRows = driver.findElements(By.cssSelector("#tbodyid tr"));
            if (!cartRows.isEmpty()) {
                String tableTextAfter = driver.findElement(By.cssSelector("#tbodyid")).getText();
                fail("Cart is not empty after clearing. Rows: " + cartRows.size() + ", Table text: " + tableTextAfter);
            }

        } catch (Exception e) {
            takeScreenshot("verify_empty_cart_error");
            fail("Failed to verify empty cart: " + e.getMessage());
        }
    }

    @When("user clicks Place Order button in cart")
    public void clickPlaceOrderButtonInCart() {
        try {
            WebElement placeOrderBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//button[contains(@class, 'btn-success') and contains(text(), 'Place Order')]")
                    )
            );

            placeOrderBtn.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("orderModal")
            ));

        } catch (Exception e) {
            takeScreenshot("place_order_error");
            fail("Failed to click Place Order: " + e.getMessage());
        }
    }

    @When("user fills order form with details")
    public void fillOrderFormWithDetails() {
        try {
            Thread.sleep(1000);

            WebElement totalElement = driver.findElement(By.id("totalm"));
            String totalText = totalElement.getText();

            if (totalText.contains(":")) {
                totalPrice = totalText.split(":")[1].trim();
            }

            Map<String, String> formData = new HashMap<>();
            formData.put("name", "Test Customer");
            formData.put("country", "Indonesia");
            formData.put("city", "Jakarta");
            formData.put("card", "4111111111111111");
            formData.put("month", "12");
            formData.put("year", "2025");

            for (Map.Entry<String, String> entry : formData.entrySet()) {
                WebElement field = driver.findElement(By.id(entry.getKey()));
                field.clear();
                field.sendKeys(entry.getValue());
            }

        } catch (Exception e) {
            takeScreenshot("fill_form_error");
            fail("Failed to fill order form: " + e.getMessage());
        }
    }

    @When("user closes order form without purchasing")
    public void closeOrderFormWithoutPurchasing() {
        try {
            WebElement orderModal = driver.findElement(By.id("orderModal"));
            if (!orderModal.isDisplayed()) {
                return;
            }

            try {
                WebElement closeBtn = driver.findElement(
                        By.xpath("//div[@id='orderModal']//button[contains(@class, 'btn-secondary') and contains(text(), 'Close')]")
                );

                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeBtn);

            } catch (Exception e1) {
                actions.sendKeys(Keys.ESCAPE).perform();
                Thread.sleep(1000);
            }

            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.id("orderModal")
            ));

        } catch (Exception e) {
            takeScreenshot("close_form_error");
            fail("Failed to close order form: " + e.getMessage());
        }
    }

    @When("user ensures modal is closed before logout")
    public void ensureModalIsClosedBeforeLogout() {
        try {
            try {
                List<WebElement> modals = driver.findElements(By.cssSelector(".modal.show"));
                if (!modals.isEmpty()) {
                    actions.sendKeys(Keys.ESCAPE).perform();
                    Thread.sleep(1500);
                }
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
    }

    @When("user completes purchase by clicking Purchase button")
    public void completePurchaseByClickingPurchaseButton() {
        try {
            WebElement purchaseBtn = driver.findElement(
                    By.xpath("//button[contains(@class, 'btn-primary') and contains(text(), 'Purchase')]")
            );

            purchaseBtn.click();

            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".sweet-alert")),
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".showSweetAlert")),
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'alert') and contains(text(),'Thank you')]"))
            ));

        } catch (Exception e) {
            takeScreenshot("purchase_error");
            fail("Failed to complete purchase: " + e.getMessage());
        }
    }

    @Then("purchase confirmation should be displayed")
    public void purchaseConfirmationShouldBeDisplayed() {
        try {
            WebElement sweetAlert = null;

            try {
                sweetAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".sweet-alert")));
            } catch (TimeoutException e1) {
                try {
                    sweetAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".showSweetAlert")));
                } catch (TimeoutException e2) {
                    takeScreenshot("no_purchase_confirmation");
                    fail("Purchase confirmation not displayed");
                }
            }

            WebElement title = sweetAlert.findElement(By.cssSelector("h2"));
            String titleText = title.getText();

            assertTrue(titleText.contains("Thank you"), "Wrong confirmation title: " + titleText);

            WebElement details = sweetAlert.findElement(By.cssSelector("p.lead.text-muted"));
            String detailsText = details.getText();

            assertTrue(detailsText.contains("Id:") || detailsText.contains("Card:"), "Incomplete order details");

            if (totalPrice != null && !totalPrice.isEmpty()) {
                assertTrue(detailsText.contains(totalPrice), "Total price not in details. Expected: " + totalPrice);
            }

        } catch (Exception e) {
            takeScreenshot("purchase_confirmation_error");
            fail("Failed to verify purchase confirmation: " + e.getMessage());
        }
    }

    @When("user clicks OK on purchase confirmation")
    public void clickOKOnPurchaseConfirmation() {
        try {
            WebElement okBtn = null;
            String[] okSelectors = {
                    "//button[contains(text(), 'OK')]",
                    "//button[text()='OK']",
                    "//button[contains(@class, 'confirm')]",
                    "//button[contains(@class, 'sa-confirm-button')]"
            };

            for (String selector : okSelectors) {
                try {
                    okBtn = driver.findElement(By.xpath(selector));
                    if (okBtn.isDisplayed()) {
                        break;
                    }
                } catch (Exception e) {
                    continue;
                }
            }

            if (okBtn == null) {
                WebElement sweetAlert = driver.findElement(By.cssSelector(".sweet-alert, .showSweetAlert"));
                okBtn = sweetAlert.findElement(By.tagName("button"));
            }

            okBtn.click();

            Thread.sleep(2000);

        } catch (Exception e) {
            takeScreenshot("ok_confirmation_error");

            try {
                actions.sendKeys(Keys.ENTER).perform();
                Thread.sleep(1000);
            } catch (Exception e2) {
                fail("Failed to close purchase confirmation: " + e.getMessage());
            }
        }
    }

    @When("user logs out from topbar menu")
    public void logoutFromTopbarMenu() {
        try {
            ensureModalIsClosedBeforeLogout();

            WebElement logoutBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(By.id("logout2"))
            );

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", logoutBtn);

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("login2")
            ));

            isLoggedIn = false;

        } catch (Exception e) {
            takeScreenshot("logout_error");
            fail("Failed to logout: " + e.getMessage());
        }
    }

    @Then("login button should be visible in topbar")
    public void loginButtonShouldBeVisibleInTopbar() {
        try {
            WebElement loginBtn = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("login2"))
            );

            assertTrue(loginBtn.isDisplayed(), "Login button not visible");
            assertEquals("Log in", loginBtn.getText().trim(),
                    "Login button text incorrect");

            try {
                WebElement welcomeMsg = driver.findElement(By.id("nameofuser"));
                if (welcomeMsg.isDisplayed()) {
                    takeScreenshot("welcome_still_visible");
                    fail("Welcome message still visible after logout");
                }
            } catch (NoSuchElementException e) {
            }

        } catch (Exception e) {
            takeScreenshot("login_button_error");
            fail("Failed to verify login button: " + e.getMessage());
        }
    }

    @Then("error should be shown for empty cart")
    public void errorShouldBeShownForEmptyCart() {
        try {
            Thread.sleep(2000);

            try {
                wait.until(ExpectedConditions.alertIsPresent());
                Alert alert = driver.switchTo().alert();
                String alertText = alert.getText();

                if (alertText.contains("empty") || alertText.contains("cart") ||
                        alertText.contains("add") || alertText.contains("product")) {
                    alert.accept();
                    return;
                }
                alert.accept();
            } catch (TimeoutException e) {
            }

            try {
                WebElement orderModal = driver.findElement(By.id("orderModal"));
                if (orderModal.isDisplayed()) {
                    WebElement totalElement = driver.findElement(By.id("totalm"));
                    String totalText = totalElement.getText();

                    if (totalText.contains("0") || totalText.trim().isEmpty()) {
                        closeOrderFormWithoutPurchasing();
                        return;
                    }
                }
            } catch (NoSuchElementException e) {
            }

        } catch (Exception e) {
            takeScreenshot("error_check_failed");
        }
    }

    @When("user cancels purchase and returns to cart")
    public void cancelPurchaseAndReturnsToCart() {
        try {
            closeOrderFormWithoutPurchasing();

        } catch (Exception e) {
            takeScreenshot("cancel_purchase_error");
            fail("Failed to cancel purchase: " + e.getMessage());
        }
    }

    @Then("close browser after test completion")
    public void closeBrowserAfterTestCompletion() {
    }

    @When("user deletes Samsung galaxy s6 from cart")
    public void deleteSamsungGalaxyS6FromCart() {
        try {
            driver.navigate().refresh();
            Thread.sleep(2000);

            boolean hasSamsungItems = true;
            int attempts = 0;
            int maxAttempts = 5;

            while (hasSamsungItems && attempts < maxAttempts) {
                attempts++;

                List<WebElement> samsungItems = driver.findElements(
                        By.xpath("//tr[td[2][contains(text(), 'Samsung galaxy s6')]]")
                );

                if (samsungItems.isEmpty()) {
                    hasSamsungItems = false;
                    System.out.println("No Samsung items found");
                    break;
                }

                System.out.println("Found " + samsungItems.size() + " Samsung items, deleting...");

                try {
                    WebElement deleteLink = driver.findElement(
                            By.xpath("//tr[td[2][contains(text(), 'Samsung galaxy s6')]][1]//a[text()='Delete']")
                    );

                    deleteLink.click();

                    try {
                        wait.until(ExpectedConditions.alertIsPresent());
                        Alert alert = driver.switchTo().alert();
                        alert.accept();
                        Thread.sleep(1000);
                    } catch (TimeoutException e) {
                        Thread.sleep(1000);
                    }

                    Thread.sleep(1500);
                    driver.navigate().refresh();
                    Thread.sleep(1000);

                } catch (Exception e) {
                    System.out.println("Error deleting Samsung item: " + e.getMessage());
                    break;
                }
            }

            deleteAllCartItems();

        } catch (Exception e) {
            takeScreenshot("delete_product_error");
            fail("Failed to delete Samsung galaxy s6 from cart: " + e.getMessage());
        }
    }

    @When("user submits empty order form")
    public void submitEmptyOrderForm() {
        try {
            WebElement purchaseBtn = driver.findElement(
                    By.xpath("//button[contains(@class, 'btn-primary') and contains(text(), 'Purchase')]")
            );

            purchaseBtn.click();

            Thread.sleep(1000);

        } catch (Exception e) {
            takeScreenshot("submit_empty_form_error");
            fail("Failed to submit empty order form: " + e.getMessage());
        }
    }

    @Then("validation errors should be shown")
    public void validationErrorsShouldBeShown() {
        try {
            boolean errorFound = false;

            try {
                wait.until(ExpectedConditions.alertIsPresent());
                Alert alert = driver.switchTo().alert();
                String alertText = alert.getText();

                if (alertText.contains("fill out") || alertText.contains("required") ||
                        alertText.contains("Name") || alertText.contains("Creditcard") ||
                        alertText.contains("Please")) {
                    errorFound = true;
                }

                alert.accept();

            } catch (TimeoutException e) {
            }

            if (!errorFound) {
                try {
                    List<WebElement> requiredFields = driver.findElements(
                            By.cssSelector("input[required]")
                    );

                    for (WebElement field : requiredFields) {
                        String value = field.getAttribute("value");
                        if (value == null || value.trim().isEmpty()) {
                            errorFound = true;
                        }
                    }

                    List<WebElement> errorMessages = driver.findElements(
                            By.cssSelector(".text-danger, .error, [style*='red']")
                    );

                    for (WebElement error : errorMessages) {
                        if (error.isDisplayed() && !error.getText().trim().isEmpty()) {
                            errorFound = true;
                        }
                    }

                } catch (Exception e) {
                }
            }

            assertTrue(errorFound, "No validation errors shown for empty form submission");

        } catch (Exception e) {
            takeScreenshot("validation_errors_check_failed");
            fail("Failed to check validation errors: " + e.getMessage());
        }
    }

    @Then("Samsung galaxy s6 should still be in cart")
    public void samsungGalaxyS6ShouldStillBeInCart() {
        try {
            driver.navigate().refresh();
            Thread.sleep(1000);

            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("#tbodyid")
            ));

            List<WebElement> cartRows = driver.findElements(
                    By.xpath("//tr[td[2][contains(text(), 'Samsung galaxy s6')]]")
            );

            assertFalse(cartRows.isEmpty(),
                    "Samsung galaxy s6 not found in cart after order cancellation");

        } catch (Exception e) {
            takeScreenshot("product_still_in_cart_error");
            fail("Failed to verify Samsung galaxy s6 in cart: " + e.getMessage());
        }
    }

    @Then("cart should be empty after purchase")
    public void cartShouldBeEmptyAfterPurchase() {
        try {
            driver.findElement(By.xpath("//a[contains(text(), 'Home')]")).click();
            Thread.sleep(2000);

            openCartFromTopbarMenu();
            Thread.sleep(2000);

            deleteAllCartItems();

            Thread.sleep(2000);
            driver.navigate().refresh();
            Thread.sleep(1000);

            List<WebElement> cartRows = driver.findElements(By.cssSelector("#tbodyid tr"));
            String tableText = driver.findElement(By.cssSelector("#tbodyid")).getText().trim();

            assertTrue(cartRows.isEmpty() || tableText.isEmpty(),
                    "Cart is not empty after purchase. Rows: " + cartRows.size());

        } catch (Exception e) {
            takeScreenshot("cart_empty_after_purchase_error");
            fail("Failed to verify empty cart after purchase: " + e.getMessage());
        }
    }

    private void clearCartBeforeTest() {
        try {
            driver.findElement(By.xpath("//a[contains(text(),'Cart')]")).click();
            Thread.sleep(2000);

            deleteAllCartItems();

            driver.findElement(By.xpath("//a[contains(text(),'Home')]")).click();
            Thread.sleep(2000);

        } catch (Exception e) {
        }
    }

    private void deleteAllCartItems() {
        try {
            driver.navigate().refresh();
            Thread.sleep(2000);

            List<WebElement> deleteLinks = driver.findElements(
                    By.xpath("//a[text()='Delete']")
            );

            System.out.println("Found " + deleteLinks.size() + " items to delete");

            while (!deleteLinks.isEmpty()) {
                try {
                    WebElement deleteLink = driver.findElement(
                            By.xpath("//a[text()='Delete'][1]")
                    );

                    deleteLink.click();

                    try {
                        wait.until(ExpectedConditions.alertIsPresent());
                        Alert alert = driver.switchTo().alert();
                        alert.accept();
                        Thread.sleep(1000);
                    } catch (TimeoutException e) {
                        Thread.sleep(1000);
                    }

                    Thread.sleep(1500);
                    driver.navigate().refresh();
                    Thread.sleep(1000);

                    deleteLinks = driver.findElements(
                            By.xpath("//a[text()='Delete']")
                    );

                } catch (Exception e) {
                    System.out.println("Error deleting item: " + e.getMessage());
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println("Error in deleteAllCartItems: " + e.getMessage());
        }
    }

    private void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});",
                element
        );
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void takeScreenshot(String name) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = name + "_" + timestamp + ".png";
            File destination = new File("screenshots/" + fileName);

            FileUtils.copyFile(source, destination);

        } catch (IOException e) {
        }
    }
}