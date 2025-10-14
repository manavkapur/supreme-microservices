package com.supremesolutions.channel_server.listener;

import com.supremesolutions.channel_server.controller.EventController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class RedisEventSubscriber implements MessageListener {

    private final EventController eventController;

    @Autowired
    public RedisEventSubscriber(EventController eventController) {
        this.eventController = eventController;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(pattern);
        String body = new String(message.getBody());

        System.out.println("📩 Redis Event Received: [" + channel + "] -> " + body);

        // Convert Redis channel to STOMP topic dynamically
        String stompTopic = switch (channel) {
            case "contact-updates" -> "/topic/contacts";
            case "quote-updates" -> "/topic/quotes";
            default -> "/topic/general";
        };

        // ✅ Broadcast via STOMP
        eventController.sendEvent(stompTopic, body);
    }
}
