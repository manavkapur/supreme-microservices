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
            String messageText = json.optString("message", "New notification");

            switch (channel) {
                case "contact-updates" -> handleContactEvent(username, messageText);
                case "quote-events" -> handleQuoteEvent(username, messageText);
                default -> System.out.println("⚠️ Unhandled channel: " + channel);
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to parse Redis message: " + e.getMessage());
        }
    }

    private void handleContactEvent(String username, String message) {
        System.out.println("📞 Handling contact event for " + username);

        // Broadcast to admin dashboards
        messagingTemplate.convertAndSend("/topic/admins", message);
        messagingTemplate.convertAndSend("/topic/contacts", message);

        // Send to user (either via WebSocket or fallback)
        sendToUser(username, "📩 New Contact Form Received", message);
    }

    private void handleQuoteEvent(String username, String message) {
        System.out.println("💰 Handling quote event for " + username);

        // Broadcast to admins
        messagingTemplate.convertAndSend("/topic/admins", message);
        messagingTemplate.convertAndSend("/topic/quotes", message);

        // Send to user (either via WebSocket or fallback)
        sendToUser(username, "📄 Quote Status Update", message);
    }

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
