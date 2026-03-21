package com.nexusqa.core;

import com.nexusqa.config.ConfigManager;
import com.nexusqa.reporting.ExtentReportManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.*;
import java.net.URL;
import java.time.Duration;

public class BaseTest {

    protected static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    protected ConfigManager config = ConfigManager.getInstance();

    @BeforeMethod
    public void setUp() throws Exception {
        String browser = config.getBrowser();
        boolean isGrid = config.isGridEnabled();
        WebDriver webDriver;

        if (isGrid) {
            // Selenium Grid - Parallel Execution
            ChromeOptions options = new ChromeOptions();
            webDriver = new RemoteWebDriver(new URL(config.get("grid.url")), options);
        } else {
            // Local Execution
            webDriver = switch (browser.toLowerCase()) {
                case "firefox" -> {
                    WebDriverManager.firefoxdriver().setup();
                    yield new FirefoxDriver();
                }
                case "edge" -> {
                    WebDriverManager.edgedriver().setup();
                    yield new EdgeDriver();
                }
                default -> {
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions options = new ChromeOptions();
                    if (config.getBoolean("headless")) {
                        options.addArguments("--headless");
                    }
                    options.addArguments("--start-maximized");
                    options.addArguments("--disable-notifications");
                    yield new ChromeDriver(options);
                }
            };
        }

        webDriver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(config.getInt("implicit.wait")));
        webDriver.manage().window().maximize();
        driver.set(webDriver);
        ExtentReportManager.logInfo("🌐 Browser: " + config.getBrowser());
        ExtentReportManager.logInfo("🔗 URL: " + config.getAppUrl());
        System.out.println("✅ Browser started: " + browser);
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    @AfterMethod
    public void tearDown() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
            System.out.println("✅ Browser closed.");
        }
    }
}