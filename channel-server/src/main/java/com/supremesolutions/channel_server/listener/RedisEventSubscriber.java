package com.supremesolutions.channel_server.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisEventSubscriber implements MessageListener {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(pattern);
        String body = new String(message.getBody());

        System.out.println("📩 Redis Event Received: [" + channel + "] -> " + body);

        switch (channel) {
            case "contact-updates" -> handleContactEvent(body);
            case "quote-updates" -> handleQuoteEvent(body);
            default -> System.out.println("⚠️ Unhandled channel: " + channel);
        }
    }

    private void handleContactEvent(String body) {
        // 1️⃣ Broadcast to all connected admin dashboards via WebSocket
        messagingTemplate.convertAndSend("/topic/admin", body);
        messagingTemplate.convertAndSend("/topic/contacts", body);

        // 2️⃣ Also publish a simpler notification event for Notification Service
        String notificationJson = String.format(
                "{\"type\":\"CONTACT_ALERT\",\"message\":\"New contact received\",\"body\":%s}",
                body
        );
        redisTemplate.convertAndSend("notification-events", notificationJson);

        System.out.println("📡 Sent contact alert → /topic/admin and Redis(notification-events)");
    }

    private void handleQuoteEvent(String body) {
        // 1️⃣ Broadcast to all dashboards subscribed to /topic/quotes
        messagingTemplate.convertAndSend("/topic/quotes", body);

        // 2️⃣ Publish notification event for Notification Service (for FCM or admin)
        String notificationJson = String.format(
                "{\"type\":\"QUOTE_UPDATE\",\"message\":\"Quote updated\",\"body\":%s}",
                body
        );
        redisTemplate.convertAndSend("notification-events", notificationJson);

        System.out.println("📡 Sent quote update → /topic/quotes and Redis(notification-events)");
    }
}
