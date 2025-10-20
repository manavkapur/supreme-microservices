package com.supremesolutions.channel_server.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class NotificationForwarder {

    private final WebClient webClient = WebClient.create("http://localhost:8085"); // Notification Service port

    public void sendMobileFallback(String username, String title, String message) {
        try {
            webClient.post()
                    .uri("/api/notify/mobile")
                    .bodyValue(
                            java.util.Map.of("username", username, "title", title, "message", message)
                    )
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(response -> System.out.println("📲 Fallback push sent: " + response));
        } catch (Exception e) {
            System.err.println("⚠️ Failed to send FCM fallback: " + e.getMessage());
        }
    }
}
