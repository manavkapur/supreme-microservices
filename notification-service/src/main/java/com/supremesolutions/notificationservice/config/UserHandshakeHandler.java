package com.supremesolutions.notificationservice.config;

import java.security.Principal;
import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

public class UserHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(
            @NonNull ServerHttpRequest request,
            @NonNull WebSocketHandler wsHandler,
            @NonNull Map<String, Object> attributes) {

        String username = (String) attributes.get("username");
        if (username == null) {
            username = "guest-" + System.currentTimeMillis();
        }

        final String finalUsername = username;
        System.out.println("🔗 WebSocket connected: " + finalUsername);
        return () -> finalUsername;
    }
}


