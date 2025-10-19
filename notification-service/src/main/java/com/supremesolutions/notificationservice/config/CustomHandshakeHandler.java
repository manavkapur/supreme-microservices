package com.supremesolutions.notificationservice.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // 🧠 Retrieve the username set by your UserHandshakeInterceptor
        String username = (String) attributes.get("username");
        if (username == null || username.isBlank()) {
            // fallback to random ID if JWT or username missing
            username = "guest-" + UUID.randomUUID();
        }

        String finalUsername = username;
        return () -> finalUsername; // Return Principal with username
    }
}
