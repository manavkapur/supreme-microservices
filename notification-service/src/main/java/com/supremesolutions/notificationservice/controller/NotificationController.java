package com.supremesolutions.notificationservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notify")
public class NotificationController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/admin")
    public String sendAdminNotification(@RequestParam String message) {
        // 🔥 Send to all admins (public topic)
        messagingTemplate.convertAndSend("/topic/admins", message);
        return "Admin notification sent!";
    }

    @PostMapping("/user")
    public String sendUserNotification(@RequestParam String username, @RequestParam String message) {
        // 🎯 Send only to a specific user
        messagingTemplate.convertAndSendToUser(username, "/queue/updates", message);
        return "User-specific notification sent!";
    }

    @PostMapping("/broadcast")
    public String broadcastNotification(@RequestParam String message) {
        // 📣 Send to everyone subscribed to /topic/updates
        messagingTemplate.convertAndSend("/topic/updates", message);
        return "Broadcast notification sent!";
    }

    // Optional STOMP handler
    @MessageMapping("/sendMessage")
    public void handleMessage(String message) {
        messagingTemplate.convertAndSend("/topic/general", message);
    }
}
