package com.supremesolutions.notificationservice.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class SessionLogger {

    private final SimpUserRegistry userRegistry;

    public SessionLogger(SimpUserRegistry userRegistry) {
        this.userRegistry = userRegistry;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        System.out.println("✅ User connected: " + sha.getSessionId());
        printActiveUsers();
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        System.out.println("❌ User disconnected: " + sha.getSessionId());
        printActiveUsers();
    }

    private void printActiveUsers() {
        System.out.println("👥 Active STOMP users (" + userRegistry.getUserCount() + "):");
        for (SimpUser user : userRegistry.getUsers()) {
            System.out.println(" - " + user.getName());
        }
    }
}
