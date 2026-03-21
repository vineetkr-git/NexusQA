package com.nexusqa.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import com.nexusqa.core.BaseTest;

import java.util.Base64;

public class ExtentReportManager {

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    // ===== FIXED: Always same file name =====
    private static final String REPORT_PATH =
            "reports/NexusQA_Latest_Report.html";

    public static ExtentReports getInstance() {
        if (extent == null) {
            ExtentSparkReporter spark =
                    new ExtentSparkReporter(REPORT_PATH);
            spark.config().setTheme(Theme.DARK);
            spark.config().setDocumentTitle("NexusQA Test Report");
            spark.config().setReportName("🚀 NexusQA Automation Report");
            spark.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");
            spark.config().setCss(
                    ".badge-primary{background:#7c4dff!important}" +
                            "pre{background:#1e1e1e;color:#d4d4d4;" +
                            "padding:10px;border-radius:5px;" +
                            "overflow-x:auto;font-size:12px}" +
                            ".request-block{background:#003366;" +
                            "padding:8px;border-radius:4px;margin:4px 0}" +
                            ".response-block{background:#003300;" +
                            "padding:8px;border-radius:4px;margin:4px 0}" +
                            "img{max-width:100%;border-radius:6px;" +
                            "border:2px solid #7c4dff;margin:5px 0}"
            );

            extent = new ExtentReports();
            extent.attachReporter(spark);

            extent.setSystemInfo("Framework",   "NexusQA v1.0");
            extent.setSystemInfo("App",          "OrangeHRM Demo");
            extent.setSystemInfo("Environment",  "Demo/QA");
            extent.setSystemInfo("Browser",      "Chrome");
            extent.setSystemInfo("OS",
                    System.getProperty("os.name"));
            extent.setSystemInfo("Java",
                    System.getProperty("java.version"));

            System.out.println("📊 Report: " + REPORT_PATH);
        }
        return extent;
    }

    public static ExtentTest createTest(String name, String desc) {
        ExtentTest t = getInstance().createTest(name, desc);
        test.set(t);
        return t;
    }

    public static ExtentTest getTest() { return test.get(); }

    // ===== Logging =====
    public static void logPass(String msg) {
        if (test.get() != null)
            test.get().log(Status.PASS, "✅ " + msg);
    }

    public static void logFail(String msg) {
        if (test.get() != null)
            test.get().log(Status.FAIL, "❌ " + msg);
    }

    public static void logInfo(String msg) {
        if (test.get() != null)
            test.get().log(Status.INFO, msg);
    }

    public static void logSkip(String msg) {
        if (test.get() != null)
            test.get().skip(msg);
    }

    public static void logCode(String label, String code) {
        if (test.get() != null)
            test.get().log(Status.INFO,
                    "<b>" + label + ":</b><pre>"
                            + escapeHtml(code) + "</pre>");
    }

    // ===== Screenshot EMBEDDED as Base64 (no file needed!) =====
    public static void takeScreenshot(String title) {
        if (test.get() == null) return;
        try {
            WebDriver driver = BaseTest.getDriver();
            if (driver == null) return;

            // Capture as Base64
            String base64 = ((TakesScreenshot) driver)
                    .getScreenshotAs(OutputType.BASE64);

            // Embed directly in report — no file saved!
            test.get().log(Status.INFO,
                    "<b>📸 " + title + "</b><br>" +
                            "<img src='data:image/png;base64,"
                            + base64 + "' " +
                            "style='max-width:100%;border-radius:6px;" +
                            "border:2px solid #7c4dff;margin:5px 0'/>");

            System.out.println("📸 Screenshot embedded: " + title);

        } catch (Exception e) {
            System.out.println("⚠️ Screenshot skipped: "
                    + e.getMessage());
        }
    }

    // ===== Screenshot on Pass/Fail =====
    public static void takeScreenshotOnPass(String testName) {
        takeScreenshot("✅ PASS — " + testName);
    }

    public static void takeScreenshotOnFail(String testName) {
        takeScreenshot("❌ FAIL — " + testName);
    }

    // ===== API Logging =====
    public static void logApiRequest(String method,
                                     String endpoint, String body) {
        if (test.get() == null) return;
        String html =
                "<div style='background:#003366;border-left:4px solid " +
                        "#448aff;padding:10px;border-radius:4px;margin:5px 0'>" +
                        "<b style='color:#4fc3f7'>🌐 API REQUEST</b><br>" +
                        "<b>Method:</b> <span style='color:#80cbc4'>"
                        + method + "</span> &nbsp;" +
                        "<b>Endpoint:</b> <code>" + endpoint + "</code>" +
                        (body != null && !body.isEmpty()
                                ? "<br><b>Body:</b><pre>"
                                + escapeHtml(prettyJson(body)) + "</pre>"
                                : "") +
                        "</div>";
        test.get().log(Status.INFO, html);
    }

    public static void logApiResponse(int statusCode,
                                      String responseBody) {
        if (test.get() == null) return;
        String color = statusCode < 300 ? "#69f0ae" :
                statusCode < 400 ? "#ffd740" : "#ff5252";
        String truncated = responseBody != null
                && responseBody.length() > 2000
                ? responseBody.substring(0, 2000) + "\n...[truncated]"
                : responseBody;
        String html =
                "<div style='background:#003300;border-left:4px solid "
                        + color + ";padding:10px;border-radius:4px;margin:5px 0'>"
                        + "<b style='color:" + color + "'>📥 API RESPONSE — "
                        + statusCode + "</b><br>"
                        + "<pre>" + escapeHtml(prettyJson(truncated))
                        + "</pre></div>";
        test.get().log(Status.INFO, html);
    }

    // ===== AI Logging =====
    public static void logAI(String agentName, String message) {
        if (test.get() == null) return;
        String html =
                "<div style='background:#1a1a2e;padding:10px;" +
                        "border-left:3px solid #7c4dff;border-radius:4px'>" +
                        "<b style='color:#ce93d8'>🤖 AI Agent: "
                        + agentName + "</b><pre>"
                        + escapeHtml(message) + "</pre></div>";
        test.get().log(Status.INFO, html);
    }

    // ===== Helpers =====
    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private static String prettyJson(String json) {
        if (json == null || json.trim().isEmpty()) return "";
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();
            Object obj = mapper.readValue(json, Object.class);
            return mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(obj);
        } catch (Exception e) {
            return json;
        }
    }

    public static void flush() {
        if (extent != null) {
            extent.flush();
            System.out.println("📊 Report saved: " + REPORT_PATH);
        }
    }

    public static String getReportPath() {
        return REPORT_PATH;
    }
}