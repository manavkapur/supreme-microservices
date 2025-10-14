package com.supremesolutions.channel_server.listener;

import com.supremesolutions.channel_server.websocket.EventWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class RedisEventSubscriber implements MessageListener {

    @Autowired
    private EventWebSocketHandler webSocketHandler;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(pattern);
        String body = new String(message.getBody());

        System.out.println("📩 Received Redis Event on [" + channel + "] -> " + body);

        // ✅ Broadcast to WebSocket clients
        webSocketHandler.broadcast(body);
    }
}
