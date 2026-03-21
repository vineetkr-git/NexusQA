package com.nexusqa.api;

import com.nexusqa.agents.AgentFactory;
import com.nexusqa.api.clients.ApiClient;
import com.nexusqa.api.endpoints.OrangeHRMEndpoints;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.qameta.allure.*;

public class AuthApiTest {

    private ApiClient apiClient;
    @Epic("OrangeHRM Application")
    @Feature("API Testing")

    @BeforeClass
    public void setup() {
        apiClient = ApiClient.getInstance();
        // Pre-warm session before all tests
        apiClient.getAuthToken();
        System.out.println("✅ API Client initialized with session");
    }

    @Test(description = "Verify session is established")
    @Story("Authentication")
    @Severity(SeverityLevel.BLOCKER)
    public void testGetAuthToken() {
        String sessionId = apiClient.getAuthToken();
        Assert.assertNotNull(sessionId, "❌ Session ID is null!");
        Assert.assertFalse(sessionId.isEmpty(),
                "❌ Session ID is empty!");
        System.out.println("✅ TC_API_001: Session established - PASSED");
        System.out.println("   Session ID: "
                + sessionId.substring(0, Math.min(20, sessionId.length()))
                + "...");
    }

    @Test(description = "Verify GET employees returns 200",
            dependsOnMethods = "testGetAuthToken")
    @Story("Employee API")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetEmployees() {
        Response response = apiClient.get(
                OrangeHRMEndpoints.EMPLOYEES + "?limit=10&offset=0");

        System.out.println("Employees Response: "
                + response.getBody().asString()
                .substring(0, Math.min(300,
                        response.getBody().asString().length())));

        Assert.assertEquals(response.getStatusCode(), 200,
                "❌ Expected 200 but got: " + response.getStatusCode());

        System.out.println("✅ TC_API_002: GET Employees - PASSED");
    }

    @Test(description = "Verify GET users returns 200",
            dependsOnMethods = "testGetAuthToken")
    @Story("User API")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetUsers() {
        Response response = apiClient.get(
                OrangeHRMEndpoints.USERS + "?limit=10&offset=0");

        System.out.println("Users status: "
                + response.getStatusCode());

        Assert.assertEquals(response.getStatusCode(), 200,
                "❌ Expected 200 but got: " + response.getStatusCode());
        System.out.println("✅ TC_API_003: GET Users - PASSED");
    }

    @Test(description = "Verify GET leave types returns 200",
            dependsOnMethods = "testGetAuthToken")
    @Story("Leave API")
    @Severity(SeverityLevel.NORMAL)
    public void testGetLeaveTypes() {
        Response response = apiClient.get(
                OrangeHRMEndpoints.LEAVE_TYPES);

        System.out.println("Leave Types status: "
                + response.getStatusCode());

        Assert.assertEquals(response.getStatusCode(), 200,
                "❌ Expected 200 but got: "
                        + response.getStatusCode());
        System.out.println("✅ TC_API_004: GET Leave Types - PASSED");
    }

    @Test(description = "AI Spy Agent reviews API response",
            dependsOnMethods = "testGetEmployees")
    @Story("AI API Review")
    @Severity(SeverityLevel.MINOR)
    public void testAiSpyAgentReviewsApiResponse() {
        Response response = apiClient.get(
                OrangeHRMEndpoints.EMPLOYEES + "?limit=5&offset=0");

        String aiReview = AgentFactory.getApiSpyAgent()
                .reviewApiResponse(
                        OrangeHRMEndpoints.EMPLOYEES,
                        response.getBody().asString()
                );

        System.out.println("\n🤖 AI Spy Agent Review:\n" + aiReview);
        Assert.assertNotNull(aiReview);
        System.out.println("✅ TC_API_005: AI Spy Agent - PASSED");
    }
}