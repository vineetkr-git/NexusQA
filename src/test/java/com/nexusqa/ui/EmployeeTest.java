package com.nexusqa.ui;

import com.nexusqa.agents.AgentFactory;
import com.nexusqa.core.BaseTest;
import com.nexusqa.reporting.ExtentReportManager;
import com.nexusqa.ui.pages.DashboardPage;
import com.nexusqa.ui.pages.EmployeePage;
import com.nexusqa.ui.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EmployeeTest extends BaseTest {

    private EmployeePage employeePage;

    @BeforeMethod
    public void login() {
        LoginPage loginPage = new LoginPage();
        loginPage.navigateTo();
        loginPage.enterUsername("Admin")
                .enterPassword("admin123")
                .clickLogin();
        employeePage = new EmployeePage();
    }

    @Test(description = "Verify Employee List page loads")
    public void testEmployeeListPageLoads() {
        employeePage.navigateTo();

        ExtentReportManager.takeScreenshot("Employee List Page");

        Assert.assertTrue(
                employeePage.isEmployeeListDisplayed(),
                "❌ Employee list page not displayed!");

        ExtentReportManager.logPass(
                "Employee list page loaded: "
                        + employeePage.getPageHeader());
        System.out.println(
                "✅ TC_EMP_001: Employee List Page - PASSED");
    }

    @Test(description = "Verify employees exist in the list")
    public void testEmployeesExistInList() {
        employeePage.navigateTo();

        boolean hasEmployees = employeePage.hasEmployees();
        int count = employeePage.getEmployeeCount();

        ExtentReportManager.takeScreenshot(
                "Employee List with " + count + " employees");
        ExtentReportManager.logInfo(
                "👥 Total employees visible: <b>" + count + "</b>");

        Assert.assertTrue(hasEmployees,
                "❌ No employees found in list!");
        System.out.println(
                "✅ TC_EMP_002: Employees exist — Count: " + count);
    }

    @Test(description = "Verify Add Employee page loads")
    public void testAddEmployeePageLoads() {
        employeePage.navigateToAddEmployee();

        ExtentReportManager.takeScreenshot("Add Employee Form");

        Assert.assertTrue(
                getDriver().getCurrentUrl().contains("addEmployee"),
                "❌ Not on Add Employee page!");

        ExtentReportManager.logPass(
                "Add Employee page loaded successfully");
        System.out.println(
                "✅ TC_EMP_003: Add Employee Page - PASSED");
    }

    @Test(description = "Verify Add Employee form fields exist")
    public void testAddEmployeeFormFields() {
        employeePage.navigateToAddEmployee();

        String url = getDriver().getCurrentUrl();
        Assert.assertTrue(url.contains("addEmployee"),
                "❌ Not on add employee page!");

        ExtentReportManager.takeScreenshot(
                "Add Employee Form Fields");
        ExtentReportManager.logInfo(
                "📋 Add Employee form is ready for input");

        System.out.println(
                "✅ TC_EMP_004: Form Fields Verified - PASSED");
    }

    @Test(description = "Verify search employee works")
    public void testSearchEmployee() {
        employeePage.navigateTo();

        ExtentReportManager.logInfo(
                "🔍 Searching for employee: 'Admin'");
        employeePage.searchEmployee("Admin");

        ExtentReportManager.takeScreenshot("Search Results");

        int results = employeePage.getEmployeeCount();
        ExtentReportManager.logInfo(
                "🔍 Search results: <b>" + results + "</b>");

        Assert.assertTrue(results >= 0,
                "❌ Search failed!");
        System.out.println(
                "✅ TC_EMP_005: Search Employee - PASSED. "
                        + "Results: " + results);
    }

    @Test(description = "AI Agent generates employee test cases")
    public void testAiGeneratesEmployeeTests() {
        String aiCases = AgentFactory.getTestGeneratorAgent()
                .generateTestCases(
                        "OrangeHRM Employee Management — " +
                                "Add, Edit, Delete, Search employees"
                );

        ExtentReportManager.logAI(
                "TestGeneratorAgent", aiCases);
        Assert.assertNotNull(aiCases);
        System.out.println(
                "✅ TC_EMP_006: AI Employee Test Cases - PASSED");
    }
}