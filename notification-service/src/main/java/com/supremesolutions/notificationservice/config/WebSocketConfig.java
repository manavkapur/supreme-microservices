package com.supremesolutions.notificationservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // ✅ Inject custom interceptor
    @Autowired
    private UserHandshakeInterceptor userHandshakeInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // ✅ Public + user-specific destinations
        config.enableSimpleBroker("/topic", "/queue", "/user");
        config.setApplicationDestinationPrefixes("/app");

        // ✅ Needed for convertAndSendToUser()
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // ✅ Allow frontend from any origin (React)
                .addInterceptors(userHandshakeInterceptor) // ✅ JWT-based handshake
                .setHandshakeHandler(new CustomHandshakeHandler()) // ✅ Uses Principal username
                .withSockJS(); // ✅ For SockJS fallback support
    }
}
