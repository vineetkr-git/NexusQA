package com.nexusqa.notifications;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SlackNotifier {

    private static SlackNotifier instance;
    private final String webhookUrl;
    private final HttpClient httpClient;

    private SlackNotifier(String webhookUrl) {
        this.webhookUrl = webhookUrl;
        this.httpClient = HttpClient.newHttpClient();
    }

    public static SlackNotifier getInstance(String webhookUrl) {
        if (instance == null) {
            instance = new SlackNotifier(webhookUrl);
        }
        return instance;
    }

    public void send(String message) {
        try {
            String payload = "{\"text\": \""
                    + message.replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    + "\"}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers
                            .ofString(payload))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("📢 Slack notified!");
            } else {
                System.err.println("❌ Slack error: "
                        + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("❌ Slack failed: "
                    + e.getMessage());
        }
    }

    public void sendPipelineStart() {
        send("🚀 *NexusQA Pipeline STARTED*\n"
                + "> Branch: main\n"
                + "> Time: " + java.time.LocalDateTime.now());
    }

    public void sendStageResult(String stage,
                                boolean passed,
                                int total,
                                int failures) {
        String icon = passed ? "✅" : "❌";
        send(icon + " *" + stage + "*\n"
                + "> Tests: " + total
                + " | Failures: " + failures);
    }

    public void sendPipelineResult(boolean passed,
                                   int totalTests,
                                   int totalFailures) {
        String icon   = passed ? "🎉" : "🔴";
        String status = passed ? "PASSED" : "FAILED";
        send(icon + " *NexusQA Pipeline " + status + "*\n"
                + "> Total Tests: " + totalTests + "\n"
                + "> Failures: " + totalFailures + "\n"
                + "> Report: Jenkins Build #latest");
    }
}