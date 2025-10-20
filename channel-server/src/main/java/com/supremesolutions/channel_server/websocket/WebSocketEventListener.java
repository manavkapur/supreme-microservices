package com.supremesolutions.channel_server.websocket;

import com.supremesolutions.channel_server.service.ActiveUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    @Autowired
    private ActiveUserService activeUserService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        System.out.println("🟢 New WebSocket session created (CONNECT event)");
    }

    @EventListener
    public void handleWebSocketConnectedListener(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        if (accessor.getUser() != null) {
            String username = accessor.getUser().getName();
            System.out.println("✅ STOMP connected for user: " + username);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = null;

        // 1️⃣ Try from accessor.getUser()
        if (accessor.getUser() != null) {
            username = accessor.getUser().getName();
        }

        // 2️⃣ Try from session attributes (more reliable with SockJS)
        if (username == null && accessor.getSessionAttributes() != null) {
            Object attr = accessor.getSessionAttributes().get("username");
            if (attr != null) username = attr.toString();
        }

        if (username != null) {
            activeUserService.removeUser(username);
            System.out.println("❌ User offline: " + username);
        } else {
            System.out.println("⚠️ Disconnected session still had no username");
        }
    }
}
