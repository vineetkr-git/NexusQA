package com.nexusqa.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class EmployeePage extends BasePage {

    // ===== Locators =====
    @FindBy(css = "a[href='/web/index.php/pim/viewEmployeeList']")
    private WebElement employeeMenuLink;

    @FindBy(css = "button.oxd-button--secondary span")
    private WebElement addEmployeeButton;

    @FindBy(css = "input.oxd-input[placeholder='First Name']")
    private WebElement firstNameField;

    @FindBy(css = "input.oxd-input[placeholder='Middle Name']")
    private WebElement middleNameField;

    @FindBy(css = "input.oxd-input[placeholder='Last Name']")
    private WebElement lastNameField;

    @FindBy(css = ".oxd-input-group .oxd-input:not([placeholder])")
    private List<WebElement> inputFields;

    @FindBy(css = "button[type='submit']")
    private WebElement saveButton;

    @FindBy(css = ".oxd-toast-content")
    private WebElement successToast;

    @FindBy(css = ".oxd-table-body .oxd-table-row")
    private List<WebElement> employeeRows;

    @FindBy(css = "input.oxd-input[placeholder='Type for hints...']")
    private WebElement searchNameField;

    @FindBy(css = "button[type='submit'].oxd-button--secondary")
    private WebElement searchButton;

    @FindBy(css = ".oxd-table-cell-actions button:first-child")
    private List<WebElement> editButtons;

    @FindBy(css = ".oxd-table-cell-actions button:last-child")
    private List<WebElement> deleteButtons;

    @FindBy(css = ".oxd-topbar-header-breadcrumb h6")
    private WebElement pageHeader;

    // ===== Navigation =====
    public EmployeePage navigateTo() {
        driver.get(com.nexusqa.config.ConfigManager
                .getInstance().getAppUrl()
                + "/web/index.php/pim/viewEmployeeList");
        wait.until(ExpectedConditions.visibilityOf(pageHeader));
        System.out.println("✅ Navigated to Employee List");
        return this;
    }

    public EmployeePage navigateToAddEmployee() {
        driver.get(com.nexusqa.config.ConfigManager
                .getInstance().getAppUrl()
                + "/web/index.php/pim/addEmployee");
        wait.until(ExpectedConditions.visibilityOf(firstNameField));
        System.out.println("✅ Navigated to Add Employee");
        return this;
    }

    // ===== Actions =====
    public EmployeePage enterFirstName(String name) {
        type(firstNameField, name);
        return this;
    }

    public EmployeePage enterMiddleName(String name) {
        type(middleNameField, name);
        return this;
    }

    public EmployeePage enterLastName(String name) {
        type(lastNameField, name);
        return this;
    }

    public EmployeePage clickSave() {
        click(saveButton);
        return this;
    }

    public boolean isSuccessToastDisplayed() {
        try {
            wait.until(ExpectedConditions
                    .visibilityOf(successToast));
            return successToast.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getSuccessMessage() {
        try {
            wait.until(ExpectedConditions
                    .visibilityOf(successToast));
            return getText(successToast);
        } catch (Exception e) {
            return "";
        }
    }

    public int getEmployeeCount() {
        try {
            wait.until(ExpectedConditions
                    .visibilityOfAllElements(employeeRows));
            return employeeRows.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public EmployeePage searchEmployee(String name) {
        try {
            wait.until(ExpectedConditions
                    .visibilityOf(searchNameField));
            type(searchNameField, name);
            click(searchButton);
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("Search error: " + e.getMessage());
        }
        return this;
    }

    public boolean isEmployeeListDisplayed() {
        try {
            wait.until(ExpectedConditions
                    .visibilityOf(pageHeader));
            return pageHeader.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getPageHeader() {
        return getText(pageHeader);
    }

    public boolean hasEmployees() {
        try {
            Thread.sleep(2000);
            return !employeeRows.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public EmployeePage addEmployee(
            String firstName, String middleName, String lastName) {
        navigateToAddEmployee();
        enterFirstName(firstName);
        enterMiddleName(middleName);
        enterLastName(lastName);
        clickSave();
        return this;
    }
}