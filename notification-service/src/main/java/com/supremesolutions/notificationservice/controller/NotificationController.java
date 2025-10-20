package com.supremesolutions.notificationservice.controller;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notify")
public class NotificationController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ✅ Fallback for offline users → Called by Channel Server
    @PostMapping("/mobile")
    public String sendMobileNotification(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String title = payload.get("title");
        String message = payload.get("message");

        System.out.println("📲 Mobile notification request received:");
        System.out.println("   User: " + username);
        System.out.println("   Title: " + title);
        System.out.println("   Message: " + message);

        try {
            // You can enhance this: fetch user FCM token from User Service
            // Example: token lookup by username → GET /api/users/fcm-token/{username}
            String mockToken = "sample_device_token"; // Placeholder for now

            Message fcmMessage = Message.builder()
                    .setToken(mockToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(message)
                            .build())
                    .build();

            // ✅ Uncomment once Firebase key configured
            // String response = FirebaseMessaging.getInstance().send(fcmMessage);
            // System.out.println("✅ FCM push sent: " + response);

        } catch (Exception e) {
            System.err.println("❌ Failed to send FCM: " + e.getMessage());
        }

        return "Mobile fallback handled for user: " + username;
    }

    // ✅ Real-time web notification — directly to a connected WebSocket user
    @PostMapping("/web")
    public String sendWebNotification(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String message = payload.get("message");

        System.out.println("💻 Web notification for " + username + ": " + message);
        messagingTemplate.convertAndSendToUser(username, "/queue/updates", message);

        return "Web notification sent to " + username;
    }

    // ✅ Admin broadcast (for dashboards)
    @PostMapping("/admin")
    public String broadcastAdmin(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        messagingTemplate.convertAndSend("/topic/admins", message);
        System.out.println("📣 Broadcasted admin alert: " + message);
        return "Admin broadcast sent!";
    }
}
