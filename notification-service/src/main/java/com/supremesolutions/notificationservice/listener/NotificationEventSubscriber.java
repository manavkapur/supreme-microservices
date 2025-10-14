package com.supremesolutions.notificationservice.listener;

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

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(pattern);
        String body = new String(message.getBody());

        System.out.println("🔔 Notification Event Received on [" + channel + "] -> " + body);

        if (body.contains("CONTACT_ALERT")) {
            // notify admin
            sendPushNotificationToAdmin("New Contact", "New contact received from Manav");
        } else if (body.contains("QUOTE_UPDATE")) {
            // notify user (simulate userId = 42 for now)
            sendPushNotificationToUser("Quote Updated", "Your quote has been approved ✅", 42L);
        } else {
            System.out.println("ℹ️ Unknown event type.");
        }
    }

    private void sendPushNotificationToAdmin(String title, String message) {
        try {
            Long adminUserId = 1L; // for example — admin ID
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
            System.err.println("❌ Failed to send FCM notification: " + e.getMessage());
        }
    }

    private void sendPushNotificationToUser(String title, String message, Long userId) {
        try {
            String userToken = getFcmToken(userId);

            if (userToken == null) {
                System.err.println("⚠️ No FCM token found for user " + userId);
                return;
            }

            var msg = com.google.firebase.messaging.Message.builder()
                    .setToken(userToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(message)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(msg);
            System.out.println("✅ FCM notification sent to User " + userId + ": " + response);
        } catch (Exception e) {
            System.err.println("❌ Failed to send FCM notification: " + e.getMessage());
        }
    }

    private String getFcmToken(Long userId) {
        try {
            String url = USER_SERVICE_URL + userId;
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            System.err.println("⚠️ Failed to fetch FCM token for userId=" + userId + ": " + e.getMessage());
            return null;
        }
    }
}
