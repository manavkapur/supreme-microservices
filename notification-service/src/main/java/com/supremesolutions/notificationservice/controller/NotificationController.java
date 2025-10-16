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

    // REST endpoint — backend can trigger a notification
    @PostMapping
    public String sendNotification(@RequestParam String user, @RequestParam String message) {
        messagingTemplate.convertAndSend("/topic/updates", user + ": " + message);
        return "Notification sent to all subscribers!";
    }

    // STOMP endpoint (optional, for chat-like functionality)
    @MessageMapping("/sendMessage")
    public void handleMessage(String message) {
        messagingTemplate.convertAndSend("/topic/updates", message);
    }
}
