package com.nexusqa.security;

import com.nexusqa.config.ConfigManager;
import com.nexusqa.reporting.ExtentReportManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ZapManager {

    private static final String ZAP_API =
            "http://localhost:8090";
    private static final String TARGET =
            "https://opensource-demo.orangehrmlive.com";

    // ===== Passive Scan (no ZAP needed — use built-in checks) =====
    public List<SecurityFinding> runPassiveScan(String url) {
        List<SecurityFinding> findings = new ArrayList<>();

        ExtentReportManager.logInfo(
                "🔒 <b>Running Security Scan on: " + url + "</b>");

        try {
            HttpURLConnection conn =
                    (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.connect();

            // Check security headers
            checkHeader(conn, "X-Frame-Options",
                    "Clickjacking protection", findings);
            checkHeader(conn, "X-Content-Type-Options",
                    "MIME sniffing protection", findings);
            checkHeader(conn, "X-XSS-Protection",
                    "XSS protection", findings);
            checkHeader(conn, "Strict-Transport-Security",
                    "HTTPS enforcement (HSTS)", findings);
            checkHeader(conn, "Content-Security-Policy",
                    "Content Security Policy", findings);
            checkHeader(conn, "Referrer-Policy",
                    "Referrer Policy", findings);

            // Check HTTPS
            if (!url.startsWith("https")) {
                findings.add(new SecurityFinding(
                        "HIGH", "No HTTPS",
                        "Application not using HTTPS",
                        "Enable SSL/TLS"));
            } else {
                findings.add(new SecurityFinding(
                        "INFO", "HTTPS Enabled",
                        "Application uses HTTPS ✅", "N/A"));
            }

            conn.disconnect();

        } catch (Exception e) {
            findings.add(new SecurityFinding(
                    "ERROR", "Scan Error",
                    e.getMessage(), "Check connectivity"));
        }

        logFindings(findings);
        return findings;
    }

    private void checkHeader(HttpURLConnection conn,
                             String header, String desc,
                             List<SecurityFinding> findings) {
        String value = conn.getHeaderField(header);
        if (value == null || value.isEmpty()) {
            findings.add(new SecurityFinding(
                    "MEDIUM",
                    "Missing Header: " + header,
                    desc + " header not found",
                    "Add " + header + " response header"));
        } else {
            findings.add(new SecurityFinding(
                    "INFO",
                    "Header OK: " + header,
                    desc + " = " + value, "N/A"));
        }
    }

    private void logFindings(List<SecurityFinding> findings) {
        StringBuilder html = new StringBuilder();
        html.append("<div style='margin:5px 0'>")
                .append("<b>🔒 Security Scan Results</b>")
                .append("<table style='width:100%;border-collapse:")
                .append("collapse;margin-top:8px;font-size:12px'>")
                .append("<thead><tr style='background:#1a237e'>")
                .append("<th style='padding:6px;color:#e8eaf6;")
                .append("border:1px solid #283593'>Severity</th>")
                .append("<th style='padding:6px;color:#e8eaf6;")
                .append("border:1px solid #283593'>Finding</th>")
                .append("<th style='padding:6px;color:#e8eaf6;")
                .append("border:1px solid #283593'>Details</th>")
                .append("<th style='padding:6px;color:#e8eaf6;")
                .append("border:1px solid #283593'>Fix</th>")
                .append("</tr></thead><tbody>");

        for (SecurityFinding f : findings) {
            String color = f.severity.equals("HIGH")   ? "#ff5252" :
                    f.severity.equals("MEDIUM") ? "#ffd740" :
                            f.severity.equals("INFO")   ? "#69f0ae" :
                                    "#9e9e9e";
            html.append("<tr>")
                    .append("<td style='padding:6px;border:1px solid ")
                    .append("#1a237e;color:").append(color).append("'>")
                    .append("<b>").append(f.severity).append("</b></td>")
                    .append("<td style='padding:6px;border:1px solid ")
                    .append("#1a237e;color:#cfd8dc'>")
                    .append(f.finding).append("</td>")
                    .append("<td style='padding:6px;border:1px solid ")
                    .append("#1a237e;color:#cfd8dc'>")
                    .append(f.details).append("</td>")
                    .append("<td style='padding:6px;border:1px solid ")
                    .append("#1a237e;color:#cfd8dc'>")
                    .append(f.fix).append("</td>")
                    .append("</tr>");

            System.out.println("[" + f.severity + "] "
                    + f.finding + " — " + f.details);
        }

        html.append("</tbody></table></div>");
        ExtentReportManager.logInfo(html.toString());
    }

    public long countBySeverity(
            List<SecurityFinding> findings, String severity) {
        return findings.stream()
                .filter(f -> f.severity.equals(severity))
                .count();
    }

    // ===== Inner class =====
    public static class SecurityFinding {
        public String severity;
        public String finding;
        public String details;
        public String fix;

        public SecurityFinding(String severity,
                               String finding, String details, String fix) {
            this.severity = severity;
            this.finding  = finding;
            this.details  = details;
            this.fix      = fix;
        }
    }
}