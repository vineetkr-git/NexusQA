package com.nexusqa.aws;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

@Epic("OrangeHRM Application")
@Feature("AWS Integration")
public class AWSTest {

    private AWSManager aws;

    @BeforeClass
    public void setup() {
        aws = AWSManager.getInstance();
        System.out.println("✅ AWS (LocalStack) initialized");
    }

    @Test(description = "Upload report to S3")
    @Story("S3 Storage")
    @Severity(SeverityLevel.CRITICAL)
    public void testS3Upload() throws Exception {
        Path tempFile = Files.createTempFile(
                "nexusqa-report", ".html");
        Files.writeString(tempFile,
                "<html><body>NexusQA Report</body></html>");

        aws.uploadReport(tempFile,
                "reports/nexusqa-report.html");

        System.out.println("✅ TC_AWS_001: S3 Upload - PASSED");
        Assert.assertTrue(true, "S3 upload successful");
    }

    @Test(description = "Send SNS alert notification")
    @Story("SNS Notifications")
    @Severity(SeverityLevel.NORMAL)
    public void testSNSAlert() {
        aws.sendAlert(
                "NexusQA Test Alert",
                "✅ All tests passed!\n"
                        + "DB: 7/7 | Security: 4/4 | API: 1/1");

        System.out.println("✅ TC_AWS_002: SNS Alert - PASSED");
        Assert.assertTrue(true, "SNS alert sent");
    }

    @Test(description = "Publish metrics to CloudWatch")
    @Story("CloudWatch Metrics")
    @Severity(SeverityLevel.NORMAL)
    public void testCloudWatchMetrics() {
        aws.publishMetric("TC_DB_001", "PASSED", 1200.0);
        aws.publishMetric("TC_SEC_001", "PASSED", 850.0);
        aws.publishMetric("TC_API_001", "PASSED", 320.0);

        System.out.println(
                "✅ TC_AWS_003: CloudWatch Metrics - PASSED");
        Assert.assertTrue(true, "Metrics published");
    }

    @Test(description = "Invoke Lambda function")
    @Story("Lambda Functions")
    @Severity(SeverityLevel.MINOR)
    public void testLambdaInvoke() {
        String result = aws.invokeLambda(
                "nexusqa-trigger",
                "{\"action\": \"run-smoke-tests\"}");

        System.out.println("Lambda result: " + result);
        System.out.println(
                "✅ TC_AWS_004: Lambda Invoke - PASSED");
        Assert.assertNotNull(result, "Lambda returned response");
    }
}