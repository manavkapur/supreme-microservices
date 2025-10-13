package com.supremesolutions.emailservice.service;

import com.supremesolutions.emailservice.dto.ContactEvent;
import com.supremesolutions.emailservice.dto.QuoteEvent;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSender {

    private final JavaMailSender mailSender;

    public EmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAdminNotification(ContactEvent ev) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("admin@supremesolutions.com"); // Replace with actual admin email
            message.setSubject("📩 New Contact Request: " + ev.getName());
            message.setText("Message from: " + ev.getName() + " (" + ev.getEmail() + ")\n\n" + ev.getMessage());

            mailSender.send(message);
            System.out.println("✅ Sent email to admin for contact: " + ev.getContactId());
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }

    public void sendQuoteUpdate(QuoteEvent ev) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("admin@supremesolutions.com"); // Admin gets copy
            message.setSubject("🔔 Quote Update: " + ev.getQuoteId());
            message.setText("Quote " + ev.getQuoteId() + " updated to status: " + ev.getStatus() +
                    "\n\nMessage: " + ev.getMessage());

            mailSender.send(message);
            System.out.println("✅ Email sent for quote update: " + ev.getQuoteId());
        } catch (Exception e) {
            System.err.println("❌ Failed to send quote email: " + e.getMessage());
        }
    }

}
