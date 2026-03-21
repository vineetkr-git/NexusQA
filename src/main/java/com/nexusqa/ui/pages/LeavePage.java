package com.nexusqa.ui.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class LeavePage extends BasePage {

    // ===== Locators =====
    @FindBy(css = ".oxd-topbar-header-breadcrumb h6")
    private WebElement pageHeader;

    @FindBy(css = ".oxd-table-body .oxd-table-row")
    private List<WebElement> leaveRows;

    @FindBy(css = ".oxd-select-text--active")
    private List<WebElement> dropdowns;

    @FindBy(css = "input.oxd-input[placeholder='yyyy-dd-mm']")
    private List<WebElement> dateFields;

    @FindBy(css = "button[type='submit']")
    private WebElement submitButton;

    @FindBy(css = ".oxd-toast-content")
    private WebElement successToast;

    @FindBy(css = ".oxd-table-filter-area")
    private WebElement filterArea;

    @FindBy(css = ".leave-type-container .oxd-select-text")
    private WebElement leaveTypeDropdown;

    @FindBy(css = ".oxd-calendar-wrapper")
    private WebElement calendar;

    // ===== Navigation =====
    public LeavePage navigateToLeaveList() {
        driver.get(com.nexusqa.config.ConfigManager
                .getInstance().getAppUrl()
                + "/web/index.php/leave/viewLeaveList");
        try {
            wait.until(ExpectedConditions.visibilityOf(pageHeader));
        } catch (Exception ignored) {}
        System.out.println("✅ Navigated to Leave List");
        return this;
    }

    public LeavePage navigateToApplyLeave() {
        driver.get(com.nexusqa.config.ConfigManager
                .getInstance().getAppUrl()
                + "/web/index.php/leave/applyLeave");
        try {
            Thread.sleep(2000);
        } catch (Exception ignored) {}
        System.out.println("✅ Navigated to Apply Leave");
        return this;
    }

    public LeavePage navigateToLeaveTypes() {
        driver.get(com.nexusqa.config.ConfigManager
                .getInstance().getAppUrl()
                + "/web/index.php/leave/leaveTypeList");
        try {
            wait.until(ExpectedConditions.visibilityOf(pageHeader));
        } catch (Exception ignored) {}
        System.out.println("✅ Navigated to Leave Types");
        return this;
    }

    // ===== Actions =====
    public boolean isPageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(pageHeader));
            return pageHeader.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getPageHeader() {
        try {
            return getText(pageHeader);
        } catch (Exception e) {
            return "";
        }
    }

    public int getLeaveRowCount() {
        try {
            Thread.sleep(2000);
            return leaveRows.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isFilterAreaDisplayed() {
        return isDisplayed(filterArea);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}