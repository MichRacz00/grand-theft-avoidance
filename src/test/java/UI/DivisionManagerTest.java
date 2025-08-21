package UI;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DivisionManagerTest {

    String testUsername = "Division";
    String testPassword = "d";
    WebDriver driver;

    @BeforeAll
    public static void setUpDriver() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }

    @Test
    void incorrectLogin() throws InterruptedException {
        driver.get("http://localhost:8080/GTA_VI");
        WebElement usernameInput = driver.findElement(By.id("userid"));
        usernameInput.sendKeys("Thisisnotcorrect");
        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.sendKeys("Thisisnotapassword");
        WebElement button = driver.findElement(By.id("login-confirm"));
        button.click();
        Thread.sleep(2000);
        // Incorrect credentials doesn't do anything
        String expectedUrl = "http://localhost:8080/GTA_VI/html/login.html";
        String actualUrl = driver.getCurrentUrl();
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void correctLogin() throws InterruptedException {
        driver.get("http://localhost:8080/GTA_VI");
        // Typing in the username
        WebElement usernameInput = driver.findElement(By.id("userid"));
        usernameInput.sendKeys(testUsername);
        // Typing in the password
        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.sendKeys(testPassword);
        driver.findElement(By.tagName("button")).click();
        Thread.sleep(2000);
        // Correct credentials take me to the storemanager page of my page
        String expectedUrl = "http://localhost:8080/GTA_VI/html/dashboard.html";
        String actualUrl = driver.getCurrentUrl();
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void canAccessStorePage() throws InterruptedException {
        correctLogin();
        driver.get("http://localhost:8080/GTA_VI/html/dashboard.html");
        WebElement analyticsButton = driver.findElement(By.id("analytics"));
        analyticsButton.click();
        String expectedUrl = "http://localhost:8080/GTA_VI/html/storemanager.html";
        String actualUrl = driver.getCurrentUrl();
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void canAccessDivisionManagerPage() throws InterruptedException {
        correctLogin();
        driver.get("http://localhost:8080/GTA_VI/html/divisionmanager.html");
        Thread.sleep(2000);
        String expectedUrl = "http://localhost:8080/GTA_VI/html/divisionmanager.html";
        String actualUrl = driver.getCurrentUrl();
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    void cannotAccessSystemAdminPage() throws InterruptedException {
        correctLogin();
        driver.get("http://localhost:8080/GTA_VI/html/systemadministrator.html");
        Thread.sleep(2000);
        String expectedUrl = "http://localhost:8080/GTA_VI/html/login.html";
        String actualUrl = driver.getCurrentUrl();
        assertEquals(expectedUrl, actualUrl);
    }
}
