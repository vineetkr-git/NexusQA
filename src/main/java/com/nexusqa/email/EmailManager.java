package com.nexusqa.email;

import com.nexusqa.config.ConfigManager;
import com.nexusqa.reporting.ExtentReportManager;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.Properties;

public class EmailManager {

    private static final ConfigManager config =
            ConfigManager.getInstance();

    public static void sendReport(
            int passed, int failed, int skipped,
            long durationMs, String reportFilePath) {

        if (!config.getBoolean("email.enabled")) {
            System.out.println("📧 Email disabled in config. Skipping.");
            return;
        }

        try {
            String host     = config.get("email.host");
            String port     = config.get("email.port");
            String username = config.get("email.username");
            String password = config.get("email.password");
            String to       = config.get("email.to");

            // Mail server properties
            Properties props = new Properties();
            props.put("mail.smtp.auth",            "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host",             host);
            props.put("mail.smtp.port",             port);
            props.put("mail.smtp.ssl.trust",        host);

            // Auth
            Session session = Session.getInstance(props,
                    new Authenticator() {
                        protected PasswordAuthentication
                        getPasswordAuthentication() {
                            return new PasswordAuthentication(
                                    username, password);
                        }
                    });

            // Build email
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username,
                    "NexusQA Framework"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));

            int total = passed + failed + skipped;
            String subject = failed == 0
                    ? "✅ NexusQA: ALL " + total + " TESTS PASSED"
                    : "❌ NexusQA: " + failed + " FAILURES in "
                    + total + " Tests";
            message.setSubject(subject);

            // Build multipart (HTML body + attachment)
            MimeMultipart multipart = new MimeMultipart();

            // HTML Body
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(
                    EmailTemplate.buildReportEmail(
                            passed, failed, skipped,
                            durationMs, reportFilePath),
                    "text/html; charset=utf-8");
            multipart.addBodyPart(htmlPart);

            // Attach HTML Report
            if (reportFilePath != null) {
                File reportFile = new File(reportFilePath);
                if (reportFile.exists()) {
                    MimeBodyPart attachPart = new MimeBodyPart();
                    attachPart.attachFile(reportFile);
                    multipart.addBodyPart(attachPart);
                    System.out.println("📎 Report attached: "
                            + reportFilePath);
                }
            }

            message.setContent(multipart);

            // Send!
            Transport.send(message);
            System.out.println("📧 Email sent to: " + to);
            ExtentReportManager.logInfo(
                    "📧 Test report emailed to: " + to);

        } catch (Exception e) {
            System.err.println("❌ Email failed: " + e.getMessage());
            ExtentReportManager.logInfo(
                    "⚠️ Email skipped: " + e.getMessage());
        }
    }
}