package com.nexusqa.aws;

import com.nexusqa.config.ConfigManager;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

import java.net.URI;
import java.nio.file.Path;
import java.time.Instant;

public class AWSManager {

    private static AWSManager instance;
    private S3Client s3;
    private SnsClient sns;
    private CloudWatchClient cloudWatch;
    private LambdaClient lambda;
    private String bucket;
    private String snsTopic;
    private String cwNamespace;

    private AWSManager() {
        ConfigManager config = ConfigManager.getInstance();

        String endpoint  = config.get("aws.endpoint",       "http://localhost:4566");
        String region    = config.get("aws.region",         "us-east-1");
        String accessKey = config.get("aws.accessKey",      "test");
        String secretKey = config.get("aws.secretKey",      "test");
        bucket      = config.get("aws.s3.bucket",           "nexusqa-reports");
        snsTopic    = config.get("aws.sns.topic",           "nexusqa-alerts");
        cwNamespace = config.get("aws.cloudwatch.namespace","NexusQA/Tests");

        StaticCredentialsProvider credentials =
                StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey));
        URI endpointUri  = URI.create(endpoint);
        Region awsRegion = Region.of(region);

        s3 = S3Client.builder()
                .endpointOverride(endpointUri)
                .credentialsProvider(credentials)
                .region(awsRegion)
                .forcePathStyle(true)
                .build();

        sns = SnsClient.builder()
                .endpointOverride(endpointUri)
                .credentialsProvider(credentials)
                .region(awsRegion)
                .build();

        cloudWatch = CloudWatchClient.builder()
                .endpointOverride(endpointUri)
                .credentialsProvider(credentials)
                .region(awsRegion)
                .build();

        lambda = LambdaClient.builder()
                .endpointOverride(endpointUri)
                .credentialsProvider(credentials)
                .region(awsRegion)
                .build();

        initResources();
    }

    public static AWSManager getInstance() {
        if (instance == null) {
            instance = new AWSManager();
        }
        return instance;
    }

    private void initResources() {
        try {
            s3.createBucket(CreateBucketRequest.builder()
                    .bucket(bucket).build());
            System.out.println("✅ S3 bucket ready: " + bucket);
        } catch (Exception e) {
            System.out.println("ℹ️ S3 bucket exists: " + bucket);
        }
        try {
            sns.createTopic(CreateTopicRequest.builder()
                    .name(snsTopic).build());
            System.out.println("✅ SNS topic ready: " + snsTopic);
        } catch (Exception e) {
            System.out.println("ℹ️ SNS topic exists: " + snsTopic);
        }
    }

    // ── S3 ────────────────────────────────────────────────────
    public void uploadReport(Path filePath, String s3Key) {
        try {
            s3.putObject(PutObjectRequest.builder()
                    .bucket(bucket).key(s3Key).build(), filePath);
            System.out.println("📤 S3 uploaded: s3://" + bucket + "/" + s3Key);
        } catch (Exception e) {
            System.err.println("❌ S3 upload failed: " + e.getMessage());
        }
    }

    public void uploadString(String content, String s3Key) {
        try {
            s3.putObject(PutObjectRequest.builder()
                            .bucket(bucket).key(s3Key).build(),
                    RequestBody.fromString(content));
            System.out.println("📤 S3 string uploaded: " + s3Key);
        } catch (Exception e) {
            System.err.println("❌ S3 string upload failed: " + e.getMessage());
        }
    }

    // ── SNS ───────────────────────────────────────────────────
    public void sendAlert(String subject, String message) {
        try {
            String topicArn = sns.createTopic(
                    CreateTopicRequest.builder().name(snsTopic).build()).topicArn();
            sns.publish(PublishRequest.builder()
                    .topicArn(topicArn)
                    .subject(subject)
                    .message(message)
                    .build());
            System.out.println("📢 SNS alert sent: " + subject);
        } catch (Exception e) {
            System.err.println("❌ SNS failed: " + e.getMessage());
        }
    }

    // ── CloudWatch ────────────────────────────────────────────
    public void publishMetric(String testName, String status, double duration) {
        try {
            cloudWatch.putMetricData(PutMetricDataRequest.builder()
                    .namespace(cwNamespace)
                    .metricData(MetricDatum.builder()
                            .metricName("TestDuration")
                            .value(duration)
                            .unit(StandardUnit.MILLISECONDS)
                            .timestamp(Instant.now())
                            .dimensions(
                                    Dimension.builder().name("TestName").value(testName).build(),
                                    Dimension.builder().name("Status").value(status).build())
                            .build())
                    .build());
            System.out.println("📊 CloudWatch: " + testName + " [" + status + "] " + duration + "ms");
        } catch (Exception e) {
            System.err.println("❌ CloudWatch failed: " + e.getMessage());
        }
    }

    // ── Lambda ────────────────────────────────────────────────
    public String invokeLambda(String functionName, String payload) {
        try {
            var response = lambda.invoke(InvokeRequest.builder()
                    .functionName(functionName)
                    .payload(SdkBytes.fromUtf8String(payload))
                    .build());
            String result = response.payload().asUtf8String();
            System.out.println("⚡ Lambda [" + functionName + "]: " + result);
            return result;
        } catch (Exception e) {
            System.out.println("⚠️ Lambda not deployed: " + functionName);
            return "{\"status\":\"lambda-not-deployed\"}";
        }
    }
}