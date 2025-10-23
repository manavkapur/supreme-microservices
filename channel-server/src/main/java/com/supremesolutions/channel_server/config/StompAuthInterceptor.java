package com.supremesolutions.channel_server.config;

import com.supremesolutions.channel_server.service.ActiveUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class StompAuthInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ActiveUserService activeUserService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // 🟢 Handle STOMP CONNECT (user login over WebSocket)
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (jwtService.validateToken(token)) {
                    // ✅ Always normalize username to lowercase
                    String username = jwtService.extractUsername(token).toLowerCase();

                    // Attach normalized username to session
                    accessor.setUser(() -> username);
                    accessor.getSessionAttributes().put("username", username);

                    activeUserService.addUser(username);
                    System.out.println("🔐 Authenticated WS: " + username);
                } else {
                    System.out.println("🚫 Invalid token during STOMP CONNECT");
                    return null;
                }
            } else {
                System.out.println("🚫 Missing Authorization header during STOMP CONNECT");
                return null;
            }
        }

        // 🔴 Handle STOMP DISCONNECT (optional fallback if triggered)
        if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            Object usernameObj = accessor.getSessionAttributes().get("username");
            if (usernameObj != null) {
                String username = usernameObj.toString().toLowerCase();
                activeUserService.removeUser(username);
                System.out.println("❌ Disconnected via STOMP: " + username);
            }
        }
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            System.out.println("🧠 Principal assigned to session: " + accessor.getUser().getName());
        }
        return message;
    }
}
