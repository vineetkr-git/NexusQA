package com.nexusqa.db;

import com.nexusqa.agents.AgentFactory;
import com.nexusqa.reporting.ExtentReportManager;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DBTest {

    private DBManager db;
    @Epic("OrangeHRM Application")
    @Feature("Database Testing")

    @BeforeClass
    public void setup() throws SQLException {
        db = DBManager.getInstance();
        db.setupTestSchema();
        System.out.println("✅ DB Test setup complete");
    }

    @Test(description = "Verify DB connection is established")
    @Story("DB Connection")
    @Severity(SeverityLevel.BLOCKER)
    public void testDBConnection() throws SQLException {
        db.connect();
        logAssertion("DB Connection", "Connected", "Connected", true);
        System.out.println("✅ TC_DB_001: DB Connection - PASSED");
    }

    @Test(description = "Verify INSERT employee works",
            dependsOnMethods = "testDBConnection")
    @Story("INSERT Operations")
    @Severity(SeverityLevel.CRITICAL)
    public void testInsertEmployee() throws SQLException {
        ExtentReportManager.logInfo("📥 <b>Inserting 4 test employees...</b>");

        int rows = db.executeUpdate(
                "INSERT INTO employees " +
                        "(first_name, last_name, email, department, salary) " +
                        "VALUES " +
                        "('John', 'Doe', 'john.doe@nexusqa.com', 'Engineering', 75000)," +
                        "('Jane', 'Smith', 'jane.smith@nexusqa.com', 'QA', 70000)," +
                        "('Bob', 'Johnson', 'bob.j@nexusqa.com', 'Engineering', 80000)," +
                        "('Alice', 'Brown', 'alice.b@nexusqa.com', 'HR', 65000)"
        );

        logAssertion("Rows Inserted", "rows > 0",
                String.valueOf(rows), rows > 0);
        Assert.assertTrue(rows > 0, "❌ No rows inserted!");
        System.out.println("✅ TC_DB_002: INSERT Employee - PASSED");
    }

    @Test(description = "Verify SELECT all employees",
            dependsOnMethods = "testInsertEmployee")
    @Story("SELECT Operations")
    @Severity(SeverityLevel.CRITICAL)
    public void testSelectAllEmployees() throws SQLException {
        ExtentReportManager.logInfo(
                "🔍 <b>Selecting all employees...</b>");

        List<Map<String, Object>> employees =
                db.executeQuery(DBQueries.SELECT_ALL_EMPLOYEES);

        logAssertion("Total Employees",
                "count >= 4", String.valueOf(employees.size()),
                employees.size() >= 4);
        Assert.assertFalse(employees.isEmpty(),
                "❌ No employees found!");
        Assert.assertTrue(employees.size() >= 4,
                "❌ Expected at least 4 employees!");

        System.out.println("✅ TC_DB_003: SELECT Employees - PASSED");
    }

    @Test(description = "Verify SELECT by department",
            dependsOnMethods = "testInsertEmployee")
    @Story("SELECT with Filter")
    @Severity(SeverityLevel.NORMAL)
    public void testSelectByDepartment() throws SQLException {
        ExtentReportManager.logInfo(
                "🔍 <b>Selecting employees by department: Engineering</b>");

        List<Map<String, Object>> engineers = db.executePrepared(
                DBQueries.SELECT_EMPLOYEES_BY_DEPT, "Engineering");

        logAssertion("Engineering Employees",
                "count == 2", String.valueOf(engineers.size()),
                engineers.size() == 2);
        Assert.assertEquals(engineers.size(), 2,
                "❌ Expected 2 engineers!");
        System.out.println(
                "✅ TC_DB_004: SELECT by Department - PASSED");
    }

    @Test(description = "Verify UPDATE salary works",
            dependsOnMethods = "testInsertEmployee")
    @Story("UPDATE Operations")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdateSalary() throws SQLException {
        // Before
        ExtentReportManager.logInfo(
                "📋 <b>BEFORE UPDATE - Fetching current salary...</b>");
        List<Map<String, Object>> before = db.executePrepared(
                DBQueries.SELECT_EMPLOYEE_BY_EMAIL,
                "john.doe@nexusqa.com");
        double oldSalary = ((Number) before.get(0)
                .get("SALARY")).doubleValue();

        ExtentReportManager.logInfo(
                "💰 Current salary: <b>" + oldSalary + "</b>");

        // Update
        ExtentReportManager.logInfo(
                "⚡ <b>EXECUTING UPDATE - New salary: 85000</b>");
        int rows = db.executePreparedUpdate(
                DBQueries.UPDATE_EMPLOYEE_SALARY,
                85000, "john.doe@nexusqa.com");

        // After
        ExtentReportManager.logInfo(
                "📋 <b>AFTER UPDATE - Verifying new salary...</b>");
        List<Map<String, Object>> after = db.executePrepared(
                DBQueries.SELECT_EMPLOYEE_BY_EMAIL,
                "john.doe@nexusqa.com");
        double newSalary = ((Number) after.get(0)
                .get("SALARY")).doubleValue();

        logAssertion("Salary Updated",
                "newSalary == 85000.0",
                oldSalary + " → " + newSalary,
                newSalary == 85000.0);

        Assert.assertEquals(rows, 1,
                "❌ Expected 1 row updated!");
        Assert.assertEquals(newSalary, 85000.0,
                "❌ Salary not updated!");
        System.out.println("✅ TC_DB_005: UPDATE Salary - PASSED");
    }

    @Test(description = "Verify COUNT query",
            dependsOnMethods = "testInsertEmployee")
    @Story("Aggregate Queries")
    @Severity(SeverityLevel.NORMAL)
    public void testCountEmployees() throws SQLException {
        ExtentReportManager.logInfo(
                "🔢 <b>Counting total employees...</b>");

        List<Map<String, Object>> result =
                db.executeQuery(DBQueries.COUNT_EMPLOYEES);

        int count = ((Number) result.get(0).get("TOTAL")).intValue();
        logAssertion("Employee Count",
                "count >= 4", String.valueOf(count), count >= 4);

        Assert.assertTrue(count >= 4,
                "❌ Expected at least 4 employees!");
        System.out.println("✅ TC_DB_006: COUNT - PASSED");
    }

    @Test(description = "AI Agent analyses DB query results",
            dependsOnMethods = "testSelectAllEmployees")
    @Story("AI DB Analysis")
    @Severity(SeverityLevel.MINOR)
    public void testAiAnalysesDBResults() throws SQLException {
        ExtentReportManager.logInfo(
                "🤖 <b>AI Agent analysing DB data...</b>");

        List<Map<String, Object>> employees =
                db.executeQuery(DBQueries.SELECT_ALL_EMPLOYEES);

        String aiAnalysis = AgentFactory.getBugAnalystAgent().ask(
                "Analyse this employee dataset and identify any " +
                        "data quality issues or anomalies: "
                        + employees.toString()
        );

        ExtentReportManager.logAI("BugAnalystAgent", aiAnalysis);
        Assert.assertNotNull(aiAnalysis);
        System.out.println("✅ TC_DB_007: AI DB Analysis - PASSED");
    }

    @AfterClass
    public void tearDown() {
        db.close();
    }

    // ===== Helper: Log Assertion to Report =====
    private void logAssertion(String assertName,
                              String expected, String actual, boolean passed) {
        String color  = passed ? "#69f0ae" : "#ff5252";
        String icon   = passed ? "✅" : "❌";
        String status = passed ? "PASS" : "FAIL";

        String html =
                "<div style='background:#0a1628;border-left:4px solid "
                        + color + ";padding:8px 12px;border-radius:4px;margin:4px 0'>"
                        + "<table style='width:100%;font-size:12px'>"
                        + "<tr>"
                        + "<td><b style='color:" + color + "'>" + icon
                        + " " + assertName + "</b></td>"
                        + "<td>Expected: <code>" + expected + "</code></td>"
                        + "<td>Actual: <code style='color:" + color + "'>"
                        + actual + "</code></td>"
                        + "<td><b style='color:" + color + "'>"
                        + status + "</b></td>"
                        + "</tr></table></div>";

        System.out.println(icon + " ASSERT [" + assertName + "] "
                + "Expected: " + expected
                + " | Actual: " + actual
                + " | " + status);

        ExtentReportManager.logInfo(html);
    }
}