package com.nexusqa.email;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmailTemplate {

    public static String buildReportEmail(
            int passed, int failed, int skipped,
            long durationMs, String reportPath) {

        int total = passed + failed + skipped;
        double passRate = total > 0
                ? Math.round((passed * 100.0 / total) * 10.0) / 10.0 : 0;

        String statusColor = failed == 0 ? "#00c853" : "#ff5252";
        String statusText  = failed == 0 ? "✅ ALL PASSED" : "❌ FAILURES DETECTED";
        String duration    = formatDuration(durationMs);

        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8">
              <style>
                body { font-family: Arial, sans-serif; background: #0a0a0a;
                       color: #e0e0e0; margin: 0; padding: 0; }
                .container { max-width: 700px; margin: 30px auto;
                             background: #1a1a2e; border-radius: 12px;
                             overflow: hidden; }
                .header { background: linear-gradient(135deg,
                           #7c4dff, #448aff);
                          padding: 30px; text-align: center; }
                .header h1 { margin: 0; font-size: 28px; color: white; }
                .header p  { margin: 5px 0 0; color: rgba(255,255,255,0.8); }
                .status-banner { background: %s; padding: 15px;
                                 text-align: center; font-size: 20px;
                                 font-weight: bold; color: white; }
                .stats { display: flex; padding: 20px; gap: 15px; }
                .stat-box { flex: 1; background: #16213e;
                            border-radius: 8px; padding: 20px;
                            text-align: center; }
                .stat-box .number { font-size: 36px; font-weight: bold;
                                    margin: 0; }
                .stat-box .label  { font-size: 13px; color: #9e9e9e;
                                    margin-top: 5px; }
                .pass   { color: #00c853; }
                .fail   { color: #ff5252; }
                .skip   { color: #ffd740; }
                .total  { color: #448aff; }
                .info-section { padding: 20px; }
                .info-row { display: flex; justify-content: space-between;
                            padding: 10px 0;
                            border-bottom: 1px solid #2a2a3e; }
                .info-row .key   { color: #9e9e9e; }
                .info-row .value { font-weight: bold; }
                .progress-bar { background: #2a2a3e; border-radius: 50px;
                                height: 12px; margin: 15px 20px;
                                overflow: hidden; }
                .progress-fill { height: 100%; border-radius: 50px;
                                 background: linear-gradient(90deg,
                                 #00c853, #448aff);
                                 width: %s%%; }
                .footer { text-align: center; padding: 20px;
                          color: #616161; font-size: 12px;
                          border-top: 1px solid #2a2a3e; }
                .ai-badge { background: #7c4dff; color: white;
                            padding: 3px 8px; border-radius: 12px;
                            font-size: 11px; }
              </style>
            </head>
            <body>
              <div class="container">
                <div class="header">
                  <h1>🚀 NexusQA Test Report</h1>
                  <p>Automated Test Execution Summary</p>
                  <p>%s</p>
                </div>

                <div class="status-banner" style="background:%s">
                  %s
                </div>

                <div class="stats">
                  <div class="stat-box">
                    <p class="number total">%d</p>
                    <p class="label">TOTAL</p>
                  </div>
                  <div class="stat-box">
                    <p class="number pass">%d</p>
                    <p class="label">PASSED</p>
                  </div>
                  <div class="stat-box">
                    <p class="number fail">%d</p>
                    <p class="label">FAILED</p>
                  </div>
                  <div class="stat-box">
                    <p class="number skip">%d</p>
                    <p class="label">SKIPPED</p>
                  </div>
                </div>

                <div class="progress-bar">
                  <div class="progress-fill"></div>
                </div>
                <p style="text-align:center;color:#9e9e9e;font-size:13px">
                  Pass Rate: <b style="color:#00c853">%s%%</b>
                </p>

                <div class="info-section">
                  <div class="info-row">
                    <span class="key">🕐 Duration</span>
                    <span class="value">%s</span>
                  </div>
                  <div class="info-row">
                    <span class="key">🌐 App Under Test</span>
                    <span class="value">OrangeHRM Demo</span>
                  </div>
                  <div class="info-row">
                    <span class="key">🖥️ Environment</span>
                    <span class="value">Demo / QA</span>
                  </div>
                  <div class="info-row">
                    <span class="key">🤖 AI Agents</span>
                    <span class="value">
                      <span class="ai-badge">5 Active</span>
                    </span>
                  </div>
                  <div class="info-row">
                    <span class="key">📊 Report</span>
                    <span class="value">%s</span>
                  </div>
                </div>

                <div class="footer">
                  <p>Generated by <b>NexusQA Framework v1.0</b>
                  with 🤖 AI Agents</p>
                  <p>%s</p>
                </div>
              </div>
            </body>
            </html>
            """.formatted(
                statusColor, passRate,
                LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss")),
                statusColor, statusText,
                total, passed, failed, skipped,
                passRate, duration, reportPath,
                LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("yyyy"))
        );
    }

    private static String formatDuration(long ms) {
        if (ms < 1000) return ms + "ms";
        if (ms < 60000) return (ms / 1000) + "s";
        return (ms / 60000) + "m " + ((ms % 60000) / 1000) + "s";
    }
}