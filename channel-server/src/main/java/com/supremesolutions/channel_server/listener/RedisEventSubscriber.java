package com.supremesolutions.channel_server.listener;

import com.supremesolutions.channel_server.service.ActiveUserService;
import com.supremesolutions.channel_server.service.NotificationForwarder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.json.JSONObject;

@Component
public class RedisEventSubscriber implements MessageListener {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ActiveUserService activeUserService;

    @Autowired
    private NotificationForwarder notificationForwarder;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(pattern);
        String body = new String(message.getBody());

        System.out.println("📩 Redis Event Received: [" + channel + "] -> " + body);

        try {
            JSONObject json = new JSONObject(body);
            String username = json.optString("username", null);
            String eventType = json.optString("event", "");

            switch (channel) {
                case "contact-updates" -> handleContactEvent(username, json);
                case "quote-events" -> handleQuoteEvent(username, json, eventType);
                default -> System.out.println("⚠️ Unhandled channel: " + channel);
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to parse Redis message: " + e.getMessage());
        }
    }
    // -----------------------------------------------
    // CONTACT EVENTS
    // -----------------------------------------------
    private void handleContactEvent(String username, JSONObject json) {
        String message = json.optString("message", "New contact received");
        System.out.println("📞 Handling contact event for " + username);

        messagingTemplate.convertAndSend("/topic/admins", message);
        messagingTemplate.convertAndSend("/topic/contacts", message);

        sendToUser(username, "📩 New Contact Form Received", message);
    }

    // -----------------------------------------------
    // QUOTE EVENTS
    // -----------------------------------------------
    private void handleQuoteEvent(String username, JSONObject json, String eventType) {
        String displayMessage;

        if ("quote.created".equals(eventType)) {
            displayMessage = json.optString("message", "Your quote was created successfully.");
        } else if ("quote.updated".equals(eventType)) {
            String status = json.optString("status", "Updated");
            displayMessage = "Your quote status is now: " + status;
        } else {
            displayMessage = "New quote event received.";
        }

        System.out.println("💰 Handling quote event for " + username + " → " + displayMessage);

        // Broadcast to admins
        messagingTemplate.convertAndSend("/topic/admins", displayMessage);
        messagingTemplate.convertAndSend("/topic/quotes", displayMessage);

        // Send to user
        sendToUser(username, "📄 Quote Status Update", displayMessage);
    }

    // -----------------------------------------------
    // SHARED LOGIC
    // -----------------------------------------------
    private void sendToUser(String username, String title, String message) {
        if (username == null) {
            System.out.println("⚠️ No username specified in event");
            return;
        }

        if (activeUserService.isOnline(username)) {
            System.out.println("🟢 User online → sending WebSocket update");
            messagingTemplate.convertAndSendToUser(username, "/queue/updates", message);
        } else {
            System.out.println("🔴 User offline → sending via Notification Service");
            notificationForwarder.sendMobileFallback(username, title, message);
        }
    }
}
