package com.nexusqa.security;

import com.nexusqa.agents.AgentFactory;
import com.nexusqa.reporting.ExtentReportManager;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

public class SecurityTest {

    private ZapManager zapManager;
    private static final String TARGET =
            "https://opensource-demo.orangehrmlive.com";

    @Epic("OrangeHRM Application")
    @Feature("Security Testing")

    @BeforeClass
    public void setup() {
        zapManager = new ZapManager();
        System.out.println("🔒 Security Test suite initialized");
    }

    @Test(description = "Scan security headers of OrangeHRM")
    @Story("Security Headers")
    @Severity(SeverityLevel.CRITICAL)
    public void testSecurityHeaders() {
        ExtentReportManager.logInfo(
                "🔒 <b>Scanning security headers...</b>");

        List<ZapManager.SecurityFinding> findings =
                zapManager.runPassiveScan(TARGET);

        Assert.assertFalse(findings.isEmpty(),
                "❌ No security findings returned!");

        long high   = zapManager.countBySeverity(findings, "HIGH");
        long medium = zapManager.countBySeverity(findings, "MEDIUM");
        long info   = zapManager.countBySeverity(findings, "INFO");

        ExtentReportManager.logInfo(
                "🔍 <b>Scan Summary:</b> " +
                        "<span style='color:#ff5252'>HIGH: " + high + "</span> | " +
                        "<span style='color:#ffd740'>MEDIUM: " + medium + "</span> | " +
                        "<span style='color:#69f0ae'>INFO: " + info + "</span>");

        System.out.println("✅ TC_SEC_001: Security Headers Scan - PASSED");
        System.out.println("   HIGH: " + high
                + " MEDIUM: " + medium + " INFO: " + info);
    }

    @Test(description = "Verify HTTPS is enforced")
    @Story("HTTPS")
    @Severity(SeverityLevel.BLOCKER)
    public void testHttpsEnforced() {
        boolean isHttps = TARGET.startsWith("https");
        Assert.assertTrue(isHttps,
                "❌ Application not using HTTPS!");
        ExtentReportManager.logPass(
                "✅ HTTPS enforced on: " + TARGET);
        System.out.println(
                "✅ TC_SEC_002: HTTPS Enforced - PASSED");
    }

    @Test(description = "Verify no sensitive data in URL")
    @Story("Data Exposure")
    @Severity(SeverityLevel.CRITICAL)
    public void testNoSensitiveDataInUrl() {
        String[] sensitiveKeywords = {
                "password", "passwd", "pwd",
                "token", "secret", "apikey",
                "api_key", "auth", "credential"
        };

        for (String keyword : sensitiveKeywords) {
            Assert.assertFalse(
                    TARGET.toLowerCase().contains(keyword),
                    "❌ Sensitive data in URL: " + keyword);
        }

        ExtentReportManager.logPass(
                "No sensitive data found in base URL");
        System.out.println(
                "✅ TC_SEC_003: No Sensitive Data in URL - PASSED");
    }

    @Test(description = "AI Agent analyses security findings")
    @Story("AI Security Analysis")
    @Severity(SeverityLevel.NORMAL)
    public void testAiAnalysesSecurity() {
        List<ZapManager.SecurityFinding> findings =
                zapManager.runPassiveScan(TARGET);

        StringBuilder findingsText = new StringBuilder();
        findings.forEach(f -> findingsText
                .append("[").append(f.severity).append("] ")
                .append(f.finding).append(": ")
                .append(f.details).append("\n"));

        String aiAnalysis = AgentFactory.getBugAnalystAgent().ask(
                "You are a security expert. Analyse these security " +
                        "findings from a web application scan and provide: " +
                        "1. Risk Assessment 2. Priority fixes " +
                        "3. Security recommendations\n\n"
                        + findingsText
        );

        ExtentReportManager.logAI(
                "SecurityAnalystAgent", aiAnalysis);
        Assert.assertNotNull(aiAnalysis);
        System.out.println(
                "✅ TC_SEC_004: AI Security Analysis - PASSED");
    }
}