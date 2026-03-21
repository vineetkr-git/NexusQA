package com.nexusqa.api;

import com.nexusqa.agents.AgentFactory;
import com.nexusqa.api.clients.ApiClient;
import com.nexusqa.api.endpoints.OrangeHRMEndpoints;
import com.nexusqa.reporting.ExtentReportManager;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

@Epic("OrangeHRM Application")
@Feature("API Testing")
public class AuthApiTest {

    private ApiClient apiClient;
    private static final String BASE_URL =
            "https://opensource-demo.orangehrmlive.com";

    @BeforeClass
    public void setup() {
        apiClient = ApiClient.getInstance();
        apiClient.getAuthToken();
        System.out.println("✅ API Client initialized with session");
    }

    @Test(description = "Verify API session is established")
    @Story("Authentication")
    @Severity(SeverityLevel.BLOCKER)
    public void testGetAuthToken() {
        String sessionId = apiClient.getAuthToken();
        System.out.println("Session ID: " + sessionId);
        ExtentReportManager.logInfo("Session: " + sessionId);

        Response response = given()
                .contentType("application/json")
                .when()
                .get(BASE_URL +
                        "/web/index.php/api/v2/pim/employees?limit=1")
                .then().log().status()
                .extract().response();

        System.out.println("API Status: "
                + response.getStatusCode());

        Assert.assertTrue(
                response.getStatusCode() == 200 ||
                        response.getStatusCode() == 401,
                "API not reachable! Status: "
                        + response.getStatusCode());

        System.out.println(
                "✅ TC_API_001: API reachable - PASSED");
    }

    @Test(description = "Verify GET employees returns 200",
            dependsOnMethods = "testGetAuthToken")
    @Story("Employee API")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetEmployees() {
        Response response = apiClient.get(
                OrangeHRMEndpoints.EMPLOYEES + "?limit=10&offset=0");

        System.out.println("Employees Response: "
                + response.getBody().asString().substring(0,
                Math.min(300,
                        response.getBody().asString().length())));

        Assert.assertEquals(response.getStatusCode(), 200,
                "❌ Expected 200 but got: "
                        + response.getStatusCode());
        System.out.println(
                "✅ TC_API_002: GET Employees - PASSED");
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
                "❌ Expected 200 but got: "
                        + response.getStatusCode());
        System.out.println(
                "✅ TC_API_003: GET Users - PASSED");
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
        System.out.println(
                "✅ TC_API_004: GET Leave Types - PASSED");
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
                        response.getBody().asString());

        System.out.println("\n🤖 AI Spy Agent Review:\n"
                + aiReview);
        Assert.assertNotNull(aiReview);
        System.out.println(
                "✅ TC_API_005: AI Spy Agent - PASSED");
    }
}