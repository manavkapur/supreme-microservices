package com.supremesolutions.channel_server.listener;

import com.supremesolutions.channel_server.service.ActiveUserService;
import com.supremesolutions.channel_server.service.NotificationForwarder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

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

            // Ensure consistency
            json.put("source", channel);
            json.put("timestamp", System.currentTimeMillis());

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
    // CONTACT EVENTS (now sends full JSON)
    // -----------------------------------------------
    private void handleContactEvent(String username, JSONObject json) {
        System.out.println("📞 Handling contact event for " + username + " → " + json.toString(2));

        // Broadcast JSON to admins and contact dashboard
        messagingTemplate.convertAndSend("/topic/admins", json.toString());
        messagingTemplate.convertAndSend("/topic/contacts", json.toString());

        // Send JSON to specific user (if logged in)
        sendToUser(username, "📩 New Contact Form Received", json.toString());
    }

    // -----------------------------------------------
    // QUOTE EVENTS (now sends full JSON)
    // -----------------------------------------------
    private void handleQuoteEvent(String username, JSONObject json, String eventType) {
        System.out.println("💰 Handling quote event for " + username + " → " + json.toString(2));

        // Send JSON to admin topics
        messagingTemplate.convertAndSend("/topic/admins", json.toString());
        messagingTemplate.convertAndSend("/topic/quotes", json.toString());

        // Send structured JSON to user WebSocket
        sendToUser(username, "📄 Quote Status Update", json.toString());
    }

    // -----------------------------------------------
    // SHARED LOGIC (now sends structured JSON)
    // -----------------------------------------------
    private void sendToUser(String username, String title, String messageJson) {
        if (username == null) {
            System.out.println("⚠️ No username specified in event");
            return;
        }

        try {
            JSONObject json = new JSONObject(messageJson);

            if (activeUserService.isOnline(username)) {
                System.out.println("🟢 User online → sending structured JSON update");
                System.out.println("📨 Sending to STOMP destination: /user/" + username + "/queue/updates");

                // 🔥 FIX: use convertAndSend (not convertAndSendToUser)
                messagingTemplate.convertAndSend("/user/" + username + "/queue/updates", messageJson);

            } else {
                System.out.println("🔴 User offline → forwarding to Notification Service");
                notificationForwarder.sendMobileFallback(username, title, messageJson);
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to send structured event: " + e.getMessage());
        }
    }
}
