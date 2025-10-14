package com.supremesolutions.emailservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

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
        if (channel.equals("contact-events")) {
            sendContactNotification(event);
        } else if (channel.equals("quote-events")) {
            sendQuoteNotification(event);
        }
    }

    private void sendContactNotification(Map<String, Object> event) {
        String name = (String) event.get("name");
        String email = (String) event.get("email");
        String message = (String) event.get("message");

        // 🔔 Send to Admin
        SimpleMailMessage adminMail = new SimpleMailMessage();
        adminMail.setTo("kapurmanav99@gmail.com");
        adminMail.setSubject("📩 New Contact Request from " + name);
        adminMail.setText("You received a contact from " + name + " (" + email + "):\n\n" + message);

        mailSender.send(adminMail);
        log.info("Sent admin notification for contact: {}", name);

        // 🆕 Send confirmation email to the user
        sendUserConfirmation(email, name);
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
        String status = (String) event.get("status");

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(userEmail);
        mail.setSubject("Your Quote Update");
        mail.setText("Your quote status has been updated to: " + status);

        mailSender.send(mail);
        log.info("Sent quote update email to: {}", userEmail);
    }
}
