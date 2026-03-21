package com.nexusqa.agents;

public class AgentFactory {

    // 5 Specialized AI Agents
    private static OllamaAgent testGeneratorAgent;
    private static OllamaAgent selfHealingAgent;
    private static OllamaAgent bugAnalystAgent;
    private static OllamaAgent reportNarratorAgent;
    private static OllamaAgent apiSpyAgent;

    // 🧠 Agent 1 - Generates test cases automatically
    public static OllamaAgent getTestGeneratorAgent() {
        if (testGeneratorAgent == null)
            testGeneratorAgent = new OllamaAgent("TestGeneratorAgent");
        return testGeneratorAgent;
    }

    // 🔧 Agent 2 - Fixes broken locators automatically
    public static OllamaAgent getSelfHealingAgent() {
        if (selfHealingAgent == null)
            selfHealingAgent = new OllamaAgent("SelfHealingAgent");
        return selfHealingAgent;
    }

    // 🐛 Agent 3 - Analyses failures and suggests root cause
    public static OllamaAgent getBugAnalystAgent() {
        if (bugAnalystAgent == null)
            bugAnalystAgent = new OllamaAgent("BugAnalystAgent");
        return bugAnalystAgent;
    }

    // 📊 Agent 4 - Writes human readable test summary
    public static OllamaAgent getReportNarratorAgent() {
        if (reportNarratorAgent == null)
            reportNarratorAgent = new OllamaAgent("ReportNarratorAgent");
        return reportNarratorAgent;
    }

    // 🔍 Agent 5 - Monitors API responses and flags anomalies
    public static OllamaAgent getApiSpyAgent() {
        if (apiSpyAgent == null)
            apiSpyAgent = new OllamaAgent("ApiSpyAgent");
        return apiSpyAgent;
    }
}