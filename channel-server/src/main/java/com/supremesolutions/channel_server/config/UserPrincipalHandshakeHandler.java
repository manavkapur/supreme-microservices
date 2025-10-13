package com.supremesolutions.channel_server.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class UserPrincipalHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {

        // Retrieve userEmail set by your WebSocketAuthHandshakeInterceptor
        String userEmail = (String) attributes.get("userEmail");

        if (userEmail == null) {
            return () -> "anonymous";
        }

        // Return a Principal whose name = user's email
        return () -> userEmail;
    }
}
