package com.nexusqa.ui;

import com.nexusqa.agents.AgentFactory;
import com.nexusqa.core.BaseTest;
import com.nexusqa.ui.pages.DashboardPage;
import com.nexusqa.ui.pages.LoginPage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("OrangeHRM Application")
@Feature("Login Module")
public class LoginTest extends BaseTest {

    @Test(description = "Verify successful login with valid credentials")
    @Story("Valid Login")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that admin can login with valid credentials")
    public void testValidLogin() {
        LoginPage loginPage = new LoginPage();
        loginPage.navigateTo();

        DashboardPage dashboard = loginPage
                .enterUsername("Admin")
                .enterPassword("admin123")
                .clickLogin();

        Assert.assertTrue(dashboard.isDashboardDisplayed(),
                "❌ Dashboard not displayed after login!");
        System.out.println("✅ TC001: Valid login - PASSED");
    }

    @Test(description = "Verify error message with invalid credentials")
    @Story("Invalid Login")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify error message shown for wrong credentials")
    public void testInvalidLogin() {
        LoginPage loginPage = new LoginPage();
        loginPage.navigateTo();

        loginPage.enterUsername("wronguser")
                .enterPassword("wrongpass")
                .clickLoginExpectingFailure();

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "❌ Error message not displayed!");
        System.out.println("✅ TC002: Invalid login error - PASSED");
    }

    @Test(description = "Verify login with empty credentials")
    @Story("Empty Credentials")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify validation error shown for empty fields")
    public void testEmptyCredentials() {
        LoginPage loginPage = new LoginPage();
        loginPage.navigateTo();

        loginPage.enterUsername("")
                .enterPassword("")
                .clickLoginExpectingFailure();

        boolean hasError = loginPage.isErrorDisplayed() ||
                loginPage.hasRequiredFieldErrors();

        Assert.assertTrue(hasError,
                "❌ Validation error not shown for empty fields!");
        System.out.println("✅ TC003: Empty credentials - PASSED");
    }

    @Test(description = "AI Agent analyses login feature")
    @Story("AI Test Generation")
    @Severity(SeverityLevel.MINOR)
    @Description("AI Agent generates test cases for login feature")
    public void testAiGeneratedTestCases() {
        String aiSuggestions = AgentFactory.getTestGeneratorAgent()
                .generateTestCases(
                        "OrangeHRM Login Page with username and password");

        System.out.println("\n🤖 AI Generated:\n" + aiSuggestions);
        Assert.assertNotNull(aiSuggestions);
        System.out.println("✅ TC004: AI Test Generator - PASSED");
    }
}