package com.nexusqa.api.clients;

import com.nexusqa.reporting.ExtentReportManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;

public class ApiClient {

    private static ApiClient instance;
    private static Map<String, String> sessionCookies = new HashMap<>();
    private static boolean sessionReady = false;
    private static final String BASE_URL =
            "https://opensource-demo.orangehrmlive.com";

    private ApiClient() {
        RestAssured.baseURI = BASE_URL;
    }

    public static ApiClient getInstance() {
        if (instance == null) instance = new ApiClient();
        return instance;
    }

    // ===== Extract ALL cookies via Selenium after login =====
    private void initSession() {
        if (sessionReady) return;

        System.out.println("🔐 Initializing session via Selenium...");
        WebDriver driver = null;

        try {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            driver = new ChromeDriver(options);

            WebDriverWait wait = new WebDriverWait(driver,
                    Duration.ofSeconds(20));

            // Login
            driver.get(BASE_URL + "/web/index.php/auth/login");
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.name("username"))).sendKeys("Admin");
            driver.findElement(By.name("password"))
                    .sendKeys("admin123");
            driver.findElement(
                    By.cssSelector("button[type='submit']")).click();

            // Wait for dashboard
            wait.until(ExpectedConditions.urlContains("dashboard"));
            Thread.sleep(2000);

            // Capture ALL cookies
            Set<org.openqa.selenium.Cookie> cookies =
                    driver.manage().getCookies();

            System.out.println("🍪 All cookies captured:");
            for (org.openqa.selenium.Cookie c : cookies) {
                sessionCookies.put(c.getName(), c.getValue());
                System.out.println("  " + c.getName() + " = "
                        + c.getValue().substring(0,
                        Math.min(c.getValue().length(), 40)));
            }

            sessionReady = true;
            System.out.println("✅ Session initialized with "
                    + sessionCookies.size() + " cookies");

        } catch (Exception e) {
            System.err.println("❌ Session init error: " + e.getMessage());
        } finally {
            if (driver != null) driver.quit();
        }
    }

    // ===== Build request with session cookies =====
    private RequestSpecification buildSpec() {
        initSession();
        RequestSpecification spec = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);

        // Add ALL session cookies
        for (Map.Entry<String, String> cookie : sessionCookies.entrySet()) {
            spec = spec.cookie(cookie.getKey(), cookie.getValue());
        }

        return spec;
    }

    // ===== HTTP Methods =====
    public Response get(String endpoint) {
        ExtentReportManager.logApiRequest("GET", endpoint, null);
        Response response = buildSpec()
                .when().get(endpoint)
                .then().log().status()
                .extract().response();
        ExtentReportManager.logApiResponse(
                response.getStatusCode(),
                response.getBody().asString());
        return response;
    }

    public Response post(String endpoint, String body) {
        ExtentReportManager.logApiRequest("POST", endpoint, body);
        Response response = buildSpec()
                .body(body)
                .when().post(endpoint)
                .then().log().status()
                .extract().response();
        ExtentReportManager.logApiResponse(
                response.getStatusCode(),
                response.getBody().asString());
        return response;
    }

    public Response put(String endpoint, String body) {
        ExtentReportManager.logApiRequest("PUT", endpoint, body);
        Response response = buildSpec()
                .body(body)
                .when().put(endpoint)
                .then().log().status()
                .extract().response();
        ExtentReportManager.logApiResponse(
                response.getStatusCode(),
                response.getBody().asString());
        return response;
    }

    public Response delete(String endpoint) {
        ExtentReportManager.logApiRequest("DELETE", endpoint, null);
        Response response = buildSpec()
                .when().delete(endpoint)
                .then().log().status()
                .extract().response();
        ExtentReportManager.logApiResponse(
                response.getStatusCode(),
                response.getBody().asString());
        return response;
    }

    // For backward compatibility
    public String getAuthToken() {
        initSession();
        return sessionCookies.getOrDefault("orangehrm", "");
    }
}