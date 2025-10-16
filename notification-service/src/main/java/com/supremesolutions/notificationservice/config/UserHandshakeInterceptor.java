package com.supremesolutions.notificationservice.config;

import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

public class UserHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // Extract username from query params (SockJS doesn’t preserve headers well)
        String query = request.getURI().getQuery();
        if (query != null && query.contains("username=")) {
            String username = query.split("username=")[1].split("&")[0];
            attributes.put("username", username);
            System.out.println("💡 WebSocket handshake for: " + username);
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // no-op
    }
}
