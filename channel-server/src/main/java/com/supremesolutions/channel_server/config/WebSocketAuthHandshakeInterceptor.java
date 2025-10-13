package com.supremesolutions.channel_server.config;

import com.supremesolutions.channel_server.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class WebSocketAuthHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    public WebSocketAuthHandshakeInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        String token = null;

        if (request.getHeaders().containsKey("Authorization")) {
            String auth = request.getHeaders().getFirst("Authorization");
            if (auth != null && auth.startsWith("Bearer ")) token = auth.substring(7);
        }

        if (token == null && request instanceof ServletServerHttpRequest srv) {
            HttpServletRequest servlet = srv.getServletRequest();
            token = servlet.getParameter("token");
        }

        if (!StringUtils.hasText(token)) {
            return false;
        }

        String email = jwtUtil.validateAndGetSubject(token);
        if (email == null) return false;

        attributes.put("userEmail", email);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {}
}
