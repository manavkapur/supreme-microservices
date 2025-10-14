package com.supremesolutions.channel_server.listener;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventSubscriber implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String body = new String(message.getBody());
        System.out.println("🔔 Notification Event Received: " + body);

        // Later: parse and send FCM push
    }
}
