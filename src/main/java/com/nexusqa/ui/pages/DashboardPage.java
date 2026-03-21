package com.nexusqa.ui.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class DashboardPage extends BasePage {

    @FindBy(css = "h6.oxd-topbar-header-breadcrumb-module")
    private WebElement pageHeader;

    @FindBy(css = ".oxd-userdropdown-tab")
    private WebElement userDropdown;

    @FindBy(css = "a[href='/web/index.php/auth/logout']")
    private WebElement logoutOption;

    @FindBy(xpath = "//p[contains(@class,'oxd-userdropdown-name')]")
    private WebElement loggedInUsername;

    // ===== Actions =====
    public boolean isDashboardDisplayed() {
        try {
            waitForVisible(pageHeader);
            return pageHeader.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getPageHeader() {
        return getText(pageHeader);
    }

    public String getLoggedInUsername() {
        click(userDropdown);
        return getText(loggedInUsername);
    }

    public LoginPage logout() {
        click(userDropdown);
        waitForVisible(logoutOption);
        click(logoutOption);
        return new LoginPage();
    }
}