package com.nexusqa.db;

import com.nexusqa.config.ConfigManager;
import com.nexusqa.reporting.ExtentReportManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBManager {

    private static DBManager instance;
    private Connection connection;
    private final ConfigManager config = ConfigManager.getInstance();

    private DBManager() {}

    public static DBManager getInstance() {
        if (instance == null) instance = new DBManager();
        return instance;
    }

    // ===== Connect =====
    public void connect() throws SQLException {
        if (connection != null && !connection.isClosed()) return;

        String url  = config.get("db.url");
        String user = config.get("db.username");
        String pass = config.get("db.password");

        connection = DriverManager.getConnection(url, user, pass);
        log("🗄️ DB Connected successfully to: " + url);
    }

    // ===== Execute Query (SELECT) =====
    public List<Map<String, Object>> executeQuery(String sql)
            throws SQLException {
        connect();
        logQuery("SELECT", sql, null);

        List<Map<String, Object>> results = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= cols; i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                }
                results.add(row);
            }
        }

        logResults(results);
        return results;
    }

    // ===== Execute Update =====
    public int executeUpdate(String sql) throws SQLException {
        connect();
        logQuery("UPDATE/INSERT/DELETE", sql, null);

        try (Statement stmt = connection.createStatement()) {
            int rows = stmt.executeUpdate(sql);
            logAffected(rows);
            return rows;
        }
    }

    // ===== Execute Prepared SELECT =====
    public List<Map<String, Object>> executePrepared(
            String sql, Object... params) throws SQLException {
        connect();
        logQuery("PREPARED SELECT", sql, params);

        List<Map<String, Object>> results = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            ResultSet rs = ps.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= cols; i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                }
                results.add(row);
            }
        }

        logResults(results);
        return results;
    }

    // ===== Execute Prepared UPDATE/INSERT/DELETE =====
    public int executePreparedUpdate(String sql, Object... params)
            throws SQLException {
        connect();
        logQuery("PREPARED UPDATE", sql, params);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            int rows = ps.executeUpdate();
            logAffected(rows);
            return rows;
        }
    }

    // ===== Schema Setup =====
    public void setupTestSchema() throws SQLException {
        connect();
        log("🏗️ Setting up test schema...");

        executeUpdate("""
            CREATE TABLE IF NOT EXISTS employees (
                id         INT PRIMARY KEY AUTO_INCREMENT,
                first_name VARCHAR(50) NOT NULL,
                last_name  VARCHAR(50) NOT NULL,
                email      VARCHAR(100) UNIQUE NOT NULL,
                department VARCHAR(50),
                salary     DECIMAL(10,2),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);

        executeUpdate("""
            CREATE TABLE IF NOT EXISTS leave_requests (
                id          INT PRIMARY KEY AUTO_INCREMENT,
                employee_id INT NOT NULL,
                leave_type  VARCHAR(50),
                start_date  DATE,
                end_date    DATE,
                status      VARCHAR(20) DEFAULT 'PENDING',
                FOREIGN KEY (employee_id) REFERENCES employees(id)
            )
        """);

        log("✅ Schema created: employees, leave_requests");
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                log("🔌 DB Connection closed");
            }
        } catch (SQLException e) {
            System.err.println("❌ DB close error: " + e.getMessage());
        }
    }

    // ===== Private Logging Helpers =====

    private void logQuery(String type, String sql, Object[] params) {
        String separator = "─".repeat(60);
        String console =
                "\n" + separator +
                        "\n🗄️  DB QUERY [" + type + "]" +
                        "\n📝 SQL    : " + sql.trim().replaceAll("\\s+", " ") +
                        (params != null && params.length > 0
                                ? "\n🔧 Params : " + formatParams(params)
                                : "") +
                        "\n" + separator;

        System.out.println(console);

        // Extent Report
        String html =
                "<div style='background:#0d1b2a;border-left:4px solid " +
                        "#448aff;padding:10px;border-radius:4px;margin:5px 0'>" +
                        "<b style='color:#448aff'>🗄️ DB QUERY [" + type + "]</b><br>" +
                        "<b>SQL:</b> <code style='color:#80cbc4'>"
                        + escapeHtml(sql.trim().replaceAll("\\s+", " "))
                        + "</code>" +
                        (params != null && params.length > 0
                                ? "<br><b>Params:</b> <code style='color:#ffcc02'>"
                                + formatParams(params) + "</code>"
                                : "") +
                        "</div>";
        ExtentReportManager.logInfo(html);
    }

    private void logResults(List<Map<String, Object>> results) {
        if (results.isEmpty()) {
            System.out.println("📭 Query returned 0 rows");
            ExtentReportManager.logInfo(
                    "📭 <b>Query Result:</b> 0 rows returned");
            return;
        }

        // Console table
        System.out.println("📊 Query Results (" + results.size() + " rows):");
        System.out.println("─".repeat(80));

        // Print headers
        String headers = String.join(" | ",
                results.get(0).keySet());
        System.out.println("  " + headers);
        System.out.println("─".repeat(80));

        // Print rows
        for (Map<String, Object> row : results) {
            String rowStr = row.values().stream()
                    .map(v -> v != null ? v.toString() : "NULL")
                    .reduce((a, b) -> a + " | " + b)
                    .orElse("");
            System.out.println("  " + rowStr);
        }
        System.out.println("─".repeat(80));
        System.out.println("  Total: " + results.size() + " row(s)");

        // Build HTML table for Extent Report
        StringBuilder html = new StringBuilder();
        html.append("<div style='margin:5px 0'>")
                .append("<b style='color:#69f0ae'>📊 Query Results: ")
                .append(results.size()).append(" row(s)</b>")
                .append("<table style='width:100%;border-collapse:collapse;")
                .append("margin-top:8px;font-size:12px'>")
                .append("<thead><tr style='background:#1a237e'>");

        // Headers
        for (String col : results.get(0).keySet()) {
            html.append("<th style='padding:6px 10px;text-align:left;")
                    .append("color:#e8eaf6;border:1px solid #283593'>")
                    .append(col).append("</th>");
        }
        html.append("</tr></thead><tbody>");

        // Rows
        for (int i = 0; i < results.size(); i++) {
            String rowBg = i % 2 == 0 ? "#0d1b2a" : "#0a1628";
            html.append("<tr style='background:").append(rowBg).append("'>");
            for (Object val : results.get(i).values()) {
                html.append("<td style='padding:6px 10px;")
                        .append("border:1px solid #1a237e;color:#cfd8dc'>")
                        .append(val != null ? val.toString() : "NULL")
                        .append("</td>");
            }
            html.append("</tr>");
        }

        html.append("</tbody></table></div>");
        ExtentReportManager.logInfo(html.toString());
    }

    private void logAffected(int rows) {
        String msg = "⚡ Rows affected: " + rows;
        System.out.println(msg);
        ExtentReportManager.logInfo(
                "<b style='color:#ffd740'>" + msg + "</b>");
    }

    private void log(String msg) {
        System.out.println(msg);
        ExtentReportManager.logInfo(msg);
    }

    private String formatParams(Object[] params) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < params.length; i++) {
            sb.append(params[i]);
            if (i < params.length - 1) sb.append(", ");
        }
        return sb.append("]").toString();
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}