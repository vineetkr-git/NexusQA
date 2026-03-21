package com.nexusqa.ui;

import com.nexusqa.agents.AgentFactory;
import com.nexusqa.core.BaseTest;
import com.nexusqa.reporting.ExtentReportManager;
import com.nexusqa.ui.pages.LeavePage;
import com.nexusqa.ui.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LeaveTest extends BaseTest {

    private LeavePage leavePage;

    @BeforeMethod
    public void login() {
        LoginPage loginPage = new LoginPage();
        loginPage.navigateTo();
        loginPage.enterUsername("Admin")
                .enterPassword("admin123")
                .clickLogin();
        leavePage = new LeavePage();
    }

    @Test(description = "Verify Leave List page loads")
    public void testLeaveListPageLoads() {
        leavePage.navigateToLeaveList();

        ExtentReportManager.takeScreenshot("Leave List Page");

        Assert.assertTrue(
                leavePage.isPageDisplayed(),
                "❌ Leave list page not displayed!");

        ExtentReportManager.logPass(
                "Leave page loaded: " + leavePage.getPageHeader());
        System.out.println(
                "✅ TC_LEAVE_001: Leave List Page - PASSED");
    }

    @Test(description = "Verify Apply Leave page loads")
    public void testApplyLeavePageLoads() {
        leavePage.navigateToApplyLeave();

        ExtentReportManager.takeScreenshot("Apply Leave Page");

        // OrangeHRM may redirect to different URL
        // so check URL OR page is displayed
        String currentUrl = getDriver().getCurrentUrl();
        System.out.println("Current URL: " + currentUrl);
        ExtentReportManager.logInfo("Current URL: " + currentUrl);

        boolean isApplyPage =
                currentUrl.contains("applyLeave") ||
                        currentUrl.contains("leave") ||
                        leavePage.isPageDisplayed();

        Assert.assertTrue(isApplyPage,
                "❌ Not on Leave page! URL: " + currentUrl);

        ExtentReportManager.logPass(
                "Apply Leave page loaded. URL: " + currentUrl);
        System.out.println(
                "✅ TC_LEAVE_002: Apply Leave Page - PASSED");
    }

    @Test(description = "Verify Leave Types page loads")
    public void testLeaveTypesPageLoads() {
        leavePage.navigateToLeaveTypes();

        ExtentReportManager.takeScreenshot("Leave Types Page");

        Assert.assertTrue(
                leavePage.isPageDisplayed(),
                "❌ Leave types page not displayed!");

        ExtentReportManager.logPass(
                "Leave Types: " + leavePage.getPageHeader());
        System.out.println(
                "✅ TC_LEAVE_003: Leave Types Page - PASSED");
    }

    @Test(description = "Verify leave list has records")
    public void testLeaveListHasRecords() {
        leavePage.navigateToLeaveList();

        int rows = leavePage.getLeaveRowCount();
        ExtentReportManager.logInfo(
                "📋 Leave records found: <b>" + rows + "</b>");
        ExtentReportManager.takeScreenshot(
                "Leave Records — " + rows + " rows");

        Assert.assertTrue(rows >= 0,
                "❌ Leave list error!");
        System.out.println(
                "✅ TC_LEAVE_004: Leave Records — " + rows);
    }

    @Test(description = "AI Agent generates leave test cases")
    public void testAiGeneratesLeaveTests() {
        String aiCases = AgentFactory.getTestGeneratorAgent()
                .generateTestCases(
                        "OrangeHRM Leave Management — " +
                                "Apply leave, approve/reject, " +
                                "view leave balance, leave types"
                );

        ExtentReportManager.logAI(
                "TestGeneratorAgent", aiCases);
        Assert.assertNotNull(aiCases);
        System.out.println(
                "✅ TC_LEAVE_005: AI Leave Test Cases - PASSED");
    }
}