package com.supremesolutions.channel_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController // ✅ use RestController so it returns JSON
public class EventController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/test/send")
    public String sendTest(@RequestBody Map<String, String> body) {
        String user = body.get("user").toLowerCase();
        String msg = body.getOrDefault("message", "Hello test");
        Map<String, Object> event = Map.of("event", "quote.created", "message", msg);

        // 🟩 Add this line here
        System.out.println("📨 Sending to STOMP destination: /user/" + user + "/queue/updates");

        messagingTemplate.convertAndSendToUser(user, "/queue/updates", event);
        return "Sent to " + user;
    }


    // Existing method — leave this as-is
    public void sendEvent(String topic, String message) {
        System.out.println("📡 Broadcasting to " + topic + ": " + message);
        messagingTemplate.convertAndSend(topic, message);
    }
}
