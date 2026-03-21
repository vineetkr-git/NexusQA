package com.nexusqa.reporting;

import com.nexusqa.email.EmailManager;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        String testName   = result.getMethod().getMethodName();
        String desc       = result.getMethod().getDescription();
        String className  = result.getTestClass().getRealClass()
                .getSimpleName();

        ExtentReportManager.createTest(
                className + " → " + testName,
                desc != null ? desc : testName
        );
        ExtentReportManager.logInfo(
                "🕐 Started: " + LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        long duration = result.getEndMillis()
                - result.getStartMillis();

        // Embedded screenshot on PASS
        ExtentReportManager.takeScreenshotOnPass(
                result.getMethod().getMethodName());

        ExtentReportManager.logPass(
                "PASSED in " + duration + "ms");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        long duration = result.getEndMillis()
                - result.getStartMillis();

        // Embedded screenshot on FAIL
        ExtentReportManager.takeScreenshotOnFail(
                result.getMethod().getMethodName());

        Throwable t = result.getThrowable();
        if (t != null) {
            ExtentReportManager.logFail(
                    "FAILED after " + duration + "ms");
            ExtentReportManager.logFail(
                    t.getClass().getSimpleName()
                            + ": " + t.getMessage());

            // Relevant stack trace
            StringBuilder stack = new StringBuilder();
            for (StackTraceElement el : t.getStackTrace()) {
                if (el.getClassName().contains("nexusqa")) {
                    stack.append(el.toString()).append("\n");
                }
            }
            if (stack.length() > 0)
                ExtentReportManager.logCode(
                        "Stack Trace", stack.toString());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String reason = result.getThrowable() != null
                ? result.getThrowable().getMessage() : "Dependency failed";
        ExtentReportManager.logSkip("SKIPPED: " + reason);
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReportManager.flush();

        int passed  = context.getPassedTests().size();
        int failed  = context.getFailedTests().size();
        int skipped = context.getSkippedTests().size();
        int total   = passed + failed + skipped;
        long duration = context.getEndDate().getTime()
                - context.getStartDate().getTime();

        System.out.println("\n" + "=".repeat(50));
        System.out.println("📊 NEXUSQA EXECUTION SUMMARY");
        System.out.println("=".repeat(50));
        System.out.println("✅ Passed  : " + passed  + "/" + total);
        System.out.println("❌ Failed  : " + failed  + "/" + total);
        System.out.println("⏭️  Skipped : " + skipped + "/" + total);
        System.out.println("⏱️  Duration: " + duration + "ms");
        System.out.println("=".repeat(50));

        EmailManager.sendReport(passed, failed, skipped,
                duration, ExtentReportManager.getReportPath());
    }
}