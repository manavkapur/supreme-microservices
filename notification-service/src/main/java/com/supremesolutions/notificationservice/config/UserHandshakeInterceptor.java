package com.supremesolutions.notificationservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class UserHandshakeInterceptor implements HandshakeInterceptor {

    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        try {
            String token = null;

            // 1️⃣ Try Authorization header (non-browser clients)
            String authHeader = request.getHeaders().getFirst("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }

            // 2️⃣ Try query parameter (browser clients)
            if (token == null) {
                URI uri = request.getURI();
                String query = uri.getQuery();
                if (query != null && query.contains("token=")) {
                    token = query.split("token=")[1].split("&")[0];
                }
            }

            // 3️⃣ Parse JWT and attach username
            if (token != null) {
                Claims claims = Jwts.parser()
                        .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                        .parseClaimsJws(token)
                        .getBody();

                String username = claims.getSubject();
                attributes.put("username", username);
                System.out.println("💡 WebSocket handshake for: " + username);
            } else {
                System.out.println("🚫 No Authorization header or token found in handshake");
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to parse JWT during handshake: " + e.getMessage());
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }
}
