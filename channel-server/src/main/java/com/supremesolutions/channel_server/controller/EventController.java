package com.supremesolutions.channel_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class EventController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // This will be called by Redis subscriber
    public void sendEvent(String topic, String message) {
        System.out.println("📡 Broadcasting to " + topic + ": " + message);
        messagingTemplate.convertAndSend(topic, message);
    }
}
