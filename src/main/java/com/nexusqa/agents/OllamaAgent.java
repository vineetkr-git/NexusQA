package com.nexusqa.agents;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusqa.config.ConfigManager;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class OllamaAgent {

    private final String ollamaUrl;
    private final String model;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String agentName;

    public OllamaAgent(String agentName) {
        ConfigManager config = ConfigManager.getInstance();
        this.ollamaUrl = config.getOllamaUrl() + "/api/generate";
        this.model = config.getOllamaModel();
        this.agentName = agentName;
    }

    public String ask(String prompt) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(ollamaUrl);
            post.setHeader("Content-Type", "application/json");

            String body = mapper.writeValueAsString(
                    mapper.createObjectNode()
                            .put("model", model)
                            .put("prompt", prompt)
                            .put("stream", false)
            );

            post.setEntity(new StringEntity(body));

            StringBuilder response = new StringBuilder();
            client.execute(post, httpResponse -> {
                String rawJson = new String(
                        httpResponse.getEntity().getContent().readAllBytes()
                );
                System.out.println("🤖 Raw Ollama response: " + rawJson);
                JsonNode json = mapper.readTree(rawJson);

                // Try multiple response field names
                if (json.has("response")) {
                    response.append(json.get("response").asText());
                } else if (json.has("message")) {
                    response.append(json.get("message").get("content").asText());
                } else {
                    response.append(rawJson);
                }
                return null;
            });

            System.out.println("🤖 [" + agentName + "] Response received.");
            return response.toString();

        } catch (Exception e) {
            System.err.println("❌ [" + agentName + "] Error: " + e.getMessage());
            return "AI Agent unavailable: " + e.getMessage();
        }
    }

    // ===== Specialized Agent Methods =====

    public String analyzeFailure(String errorMessage) {
        String prompt = """
            You are a QA expert AI Agent called '%s'.
            Analyze this test failure and suggest the root cause and fix:
            
            ERROR: %s
            
            Provide:
            1. Root Cause
            2. Suggested Fix
            3. Prevention Strategy
            """.formatted(agentName, errorMessage);
        return ask(prompt);
    }

    public String generateTestCases(String featureDescription) {
        String prompt = """
            You are a QA expert AI Agent called '%s'.
            Generate TDD test cases for this feature:
            
            FEATURE: %s
            
            Provide test cases in this format:
            - Test Case Name
            - Preconditions
            - Steps
            - Expected Result
            """.formatted(agentName, featureDescription);
        return ask(prompt);
    }

    public String reviewApiResponse(String endpoint, String response) {
        String prompt = """
            You are an API testing AI Agent called '%s'.
            Review this API response for anomalies:
            
            ENDPOINT: %s
            RESPONSE: %s
            
            Check for:
            1. Missing fields
            2. Unexpected values
            3. Security issues
            4. Performance concerns
            """.formatted(agentName, endpoint, response);
        return ask(prompt);
    }
}