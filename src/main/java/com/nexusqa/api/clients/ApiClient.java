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

        System.out.println("🔐 Initializing API session...");

        // Try direct HTTP login first (works in Jenkins/headless)
        try {
            Response loginResponse = given()
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("username", "Admin")
                    .formParam("password", "admin123")
                    .redirects().follow(true)
                    .when()
                    .post(BASE_URL + "/web/index.php/auth/validateCredentials")
                    .then()
                    .extract().response();

            // Extract session cookie
            String cookie = loginResponse.getCookie("orangehrm");
            if (cookie != null && !cookie.isEmpty()) {
                sessionCookies.put("orangehrm", cookie);
                sessionReady = true;
                System.out.println("✅ Session via HTTP login: "
                        + cookie.substring(0, 10) + "...");
                return;
            }

            // Try all cookies from response
            loginResponse.cookies().forEach((k, v) -> {
                sessionCookies.put(k, v);
                System.out.println("🍪 Cookie: " + k + " = "
                        + v.substring(0, Math.min(v.length(), 15)));
            });

            if (!sessionCookies.isEmpty()) {
                sessionReady = true;
                System.out.println("✅ Session initialized via HTTP!");
                return;
            }

        } catch (Exception e) {
            System.out.println("⚠️ HTTP login attempt: " + e.getMessage());
        }

        // Fallback — try Selenium only if available
        try {
            String display = System.getenv("DISPLAY");
            boolean isHeadlessEnv = (display == null || display.isEmpty());

            if (!isHeadlessEnv || isSeleniumAvailable()) {
                initSessionViaSelenium();
            } else {
                System.out.println("⚠️ No display/Chrome available." +
                        " Using cookie-less mode.");
                sessionReady = true;
            }
        } catch (Exception e) {
            System.out.println("⚠️ Selenium unavailable: "
                    + e.getMessage());
            sessionReady = true;
        }
    }

    private boolean isSeleniumAvailable() {
        try {
            Runtime.getRuntime().exec("google-chrome --version");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void initSessionViaSelenium() {
        System.out.println("🌐 Using Selenium for session...");
        WebDriver driver = null;
        try {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new", "--no-sandbox",
                    "--disable-dev-shm-usage", "--disable-gpu");
            driver = new ChromeDriver(options);
            WebDriverWait wait = new WebDriverWait(driver,
                    Duration.ofSeconds(20));

            driver.get(BASE_URL + "/web/index.php/auth/login");
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.name("username"))).sendKeys("Admin");
            driver.findElement(By.name("password"))
                    .sendKeys("admin123");
            driver.findElement(
                    By.cssSelector("button[type='submit']")).click();
            wait.until(ExpectedConditions.urlContains("dashboard"));
            Thread.sleep(2000);

            driver.manage().getCookies().forEach(c ->
                    sessionCookies.put(c.getName(), c.getValue()));

            sessionReady = true;
            System.out.println("✅ Session via Selenium: "
                    + sessionCookies.size() + " cookies");
        } catch (Exception e) {
            System.err.println("❌ Selenium session error: "
                    + e.getMessage());
            sessionReady = true;
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