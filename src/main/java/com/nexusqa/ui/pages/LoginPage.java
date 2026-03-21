package com.nexusqa.ui.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends BasePage {

    // ===== Locators =====
    @FindBy(name = "username")
    private WebElement usernameField;

    @FindBy(name = "password")
    private WebElement passwordField;

    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;

    @FindBy(css = ".oxd-alert-content-text")
    private WebElement errorMessage;

    @FindBy(css = ".oxd-topbar-header-title")
    private WebElement dashboardTitle;

    // ===== Actions =====
    public LoginPage navigateTo() {
        driver.get(com.nexusqa.config.ConfigManager
                .getInstance().getAppUrl() + "/web/index.php/auth/login");
        System.out.println("✅ Navigated to OrangeHRM Login page");
        return this;
    }

    public LoginPage enterUsername(String username) {
        type(usernameField, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(passwordField, password);
        return this;
    }

    public DashboardPage clickLogin() {
        click(loginButton);
        return new DashboardPage();
    }

    public LoginPage clickLoginExpectingFailure() {
        click(loginButton);
        return this;
    }

    public LoginPage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        return this;
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }

    public boolean isErrorDisplayed() {
        return isDisplayed(errorMessage);
    }

    @FindBy(css = ".oxd-input-field-error-message")
    private java.util.List<WebElement> fieldErrors;

    public boolean hasRequiredFieldErrors() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    org.openqa.selenium.By.cssSelector(".oxd-input-field-error-message")));
            return !fieldErrors.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}