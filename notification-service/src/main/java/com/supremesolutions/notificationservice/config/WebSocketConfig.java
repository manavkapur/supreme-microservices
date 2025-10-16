package com.supremesolutions.notificationservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // ✅ include both public and user destinations
        config.enableSimpleBroker("/topic", "/queue", "/user");
        config.setApplicationDestinationPrefixes("/app");

        // ✅ this is critical — ensures convertAndSendToUser() resolves to /user/<username>/...
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .addInterceptors(new UserHandshakeInterceptor())
                .setAllowedOrigins("http://localhost:3000")
                .setHandshakeHandler(new UserHandshakeHandler()) // 👈 Add this
                .withSockJS();
    }
}
