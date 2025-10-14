package com.supremesolutions.notificationservice.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NotificationEventSubscriber implements MessageListener {

    @Autowired
    private RestTemplate restTemplate;

    private static final String USER_SERVICE_URL = "http://user-service/api/users/fcm-token/";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(pattern);
        String body = new String(message.getBody());
        System.out.println("🔔 Notification Event Received on [" + channel + "] -> " + body);

        try {
            JsonNode root = objectMapper.readTree(body);
            String type = root.path("type").asText();

            switch (type) {
                case "CONTACT_ALERT" -> sendPushNotificationToAdmin(
                        "New Contact",
                        root.path("message").asText("New contact received")
                );
                case "QUOTE_UPDATE" -> {
                    JsonNode eventBody = root.path("body");
                    sendPushNotificationToUserDynamic(eventBody);
                }
                default -> System.out.println("ℹ️ Unknown event type: " + type);
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to process notification event: " + e.getMessage());
        }
    }

    private void sendPushNotificationToUserDynamic(JsonNode eventBody) {
        try {
            // Try userId first
            Long userId = eventBody.has("userId") ? eventBody.get("userId").asLong() : 1L;

            // Fallback: try by email (optional future improvement)
            String userEmail = eventBody.path("email").asText(null);

            // Get token
            String userToken = getFcmToken(userId);
            if (userToken == null) {
                System.err.println("⚠️ No FCM token found for user " + userId + " (email: " + userEmail + ")");
                return;
            }

            String name = eventBody.path("name").asText("User");
            String messageText = eventBody.path("message").asText("Your quote has been updated ✅");

            var msg = com.google.firebase.messaging.Message.builder()
                    .setToken(userToken)
                    .setNotification(Notification.builder()
                            .setTitle("Quote Update for " + name)
                            .setBody(messageText)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(msg);
            System.out.println("✅ FCM notification sent to User " + userId + ": " + response);

        } catch (Exception e) {
            System.err.println("❌ Failed to send user FCM: " + e.getMessage());
        }
    }

    private void sendPushNotificationToAdmin(String title, String message) {
        try {
            Long adminUserId = 1L;
            String adminToken = getFcmToken(adminUserId);

            if (adminToken == null) {
                System.err.println("⚠️ No FCM token found for admin.");
                return;
            }

            var msg = com.google.firebase.messaging.Message.builder()
                    .setToken(adminToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(message)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(msg);
            System.out.println("✅ FCM notification sent to Admin: " + response);

        } catch (Exception e) {
            System.err.println("❌ Failed to send admin FCM: " + e.getMessage());
        }
    }

    private String getFcmToken(Long userId) {
        try {
            String url = USER_SERVICE_URL + userId;
            String response = restTemplate.getForObject(url, String.class);

            JsonNode jsonNode = objectMapper.readTree(response);
            if (jsonNode.has("fcmToken")) {
                return jsonNode.get("fcmToken").asText();
            }

            System.err.println("⚠️ FCM token not found in response: " + response);
            return null;

        } catch (Exception e) {
            System.err.println("⚠️ Failed to fetch FCM token for userId=" + userId + ": " + e.getMessage());
            return null;
        }
    }
}
