package com.supremesolutions.emailservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailEventListener {

    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void onMessage(String message, String channel) {
        try {
            log.info("Received event on {}: {}", channel, message);
            Map<String, Object> event = objectMapper.readValue(message, Map.class);
            handleEvent(event, channel);
        } catch (Exception e) {
            log.error("Error processing email event: {}", e.getMessage(), e);
        }
    }

    private void handleEvent(Map<String, Object> event, String channel) {
        if (channel.equals("contact-events") || channel.equals("contact-updates")) {
            sendContactNotification(event);
        } else if (channel.equals("quote-events")) {
            sendQuoteNotification(event);
        }
    }

    private void sendContactNotification(Map<String, Object> event) {
        String name = (String) event.getOrDefault("name", "User");
        String email = (String) event.getOrDefault("email", "unknown");
        String message = (String) event.getOrDefault("message", "(no message)");

        // --- 1️⃣ Notify Admin ---
        SimpleMailMessage adminMail = new SimpleMailMessage();
        adminMail.setFrom("info@supremebuildsolutions.com");
        adminMail.setTo("kapurmanav99@gmail.com"); // your admin email
        adminMail.setSubject("📬 New Contact Form Submission from " + name);
        adminMail.setText("Hello Admin,\n\n"
                + "A new contact request has been submitted on your website.\n\n"
                + "👤 Name: " + name + "\n"
                + "📧 Email: " + email + "\n"
                + "💬 Message:\n" + message + "\n\n"
                + "🕒 Received on: " + java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")) + "\n\n"
                + "Please respond to the user at the above email address.\n\n"
                + "— Supreme Build Solutions Notification Service");

        mailSender.send(adminMail);
        log.info("📨 Sent admin notification for new contact from: {}", email);

        // --- 2️⃣ Send Confirmation to User ---
        SimpleMailMessage userMail = new SimpleMailMessage();
        userMail.setFrom("info@supremebuildsolutions.com");
        userMail.setTo(email);
        userMail.setSubject("We’ve received your message ✅");
        userMail.setText("Hi " + name + ",\n\n"
                + "Thank you for contacting Supreme Build Solutions!\n"
                + "We’ve received your message and our team will get back to you soon.\n\n"
                + "Your message:\n" + message + "\n\n"
                + "Received on: " + java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")) + "\n\n"
                + "Best Regards,\n"
                + "Team Supreme Build Solutions\n"
                + "📧 info@supremebuildsolutions.com\n"
                + "🌐 www.supremebuildsolutions.com");

        mailSender.send(userMail);
        log.info("✅ Sent confirmation email to user: {}", email);
    }


    // 🆕 New method for user confirmation
    private void sendUserConfirmation(String userEmail, String name) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(userEmail);
            mail.setSubject("We’ve received your request ✅");
            mail.setText("Hi " + name + ",\n\n"
                    + "Thank you for reaching out to Supreme Build Solution.\n"
                    + "Our team has received your message and will get back to you shortly.\n\n"
                    + "Best regards,\n"
                    + "Supreme Build Solution Team");

            mailSender.send(mail);
            log.info("Sent confirmation email to user: {}", userEmail);
        } catch (Exception e) {
            log.error("Failed to send confirmation email to user {}: {}", userEmail, e.getMessage());
        }
    }

    private void sendQuoteNotification(Map<String, Object> event) {
        String userEmail = (String) event.get("email");
        String name = (String) event.getOrDefault("name", "User");
        String message = (String) event.getOrDefault("message", "");
        String status = (String) event.getOrDefault("status", "Created");
        Object quoteIdObj = event.get("quoteId");

        // Extract quoteId safely
        Long quoteId = 0L;
        if (quoteIdObj instanceof Number) {
            quoteId = ((Number) quoteIdObj).longValue();
        } else if (quoteIdObj instanceof java.util.List<?> list && list.size() > 1) {
            Object idValue = list.get(1);
            if (idValue instanceof Number) {
                quoteId = ((Number) idValue).longValue();
            } else if (idValue instanceof String) {
                try {
                    quoteId = Long.parseLong((String) idValue);
                } catch (NumberFormatException ignored) {}
            }
        }

        // --- 1️⃣ User email ---
        SimpleMailMessage userMail = new SimpleMailMessage();
        userMail.setFrom("info@supremebuildsolutions.com");
        userMail.setTo(userEmail);
        userMail.setSubject("Your Quote Update #" + quoteId);
        userMail.setText("Hi " + name + ",\n\n"
                + "Thank you for reaching out to Supreme Build Solutions.\n"
                + "Your quote has been received successfully and is currently marked as: " + status + ".\n"
                + "Our team will review your details and contact you soon.\n\n"
                + "Quote Details:\n"
                + "Message: " + message + "\n"
                + "Reference ID: " + quoteId + "\n"
                + "Sent on: " + java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")) + "\n\n"
                + "Best Regards,\n"
                + "Team Supreme Build Solutions");

        mailSender.send(userMail);
        log.info("✅ Sent quote confirmation email to user: {} for quote {}", userEmail, quoteId);

        // --- 2️⃣ Admin notification ---
        SimpleMailMessage adminMail = new SimpleMailMessage();
        adminMail.setFrom("info@supremebuildsolutions.com");
        adminMail.setTo("kapursushmita786@gmail.com"); // ✅ Admin email here
        adminMail.setSubject("📩 New Quote Received #" + quoteId);
        adminMail.setText("A new quote has been submitted via your website.\n\n"
                + "👤 Name: " + name + "\n"
                + "📧 Email: " + userEmail + "\n"
                + "💬 Message: " + message + "\n"
                + "🆔 Quote ID: " + quoteId + "\n"
                + "🕒 Received on: " + java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")) + "\n\n"
                + "Please review it in your admin panel.");

        mailSender.send(adminMail);
        log.info("📨 Sent admin notification for new quote: {}", quoteId);
    }

}
