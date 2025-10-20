package com.supremesolutions.channel_server.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class NotificationForwarder {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8085") // Notification Service port
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .build();

    public void sendMobileFallback(String username, String title, String message) {
        try {
            System.out.println("📤 Sending fallback notification to Notification Service:");
            System.out.println("   username=" + username);
            System.out.println("   title=" + title);
            System.out.println("   message=" + message);

            webClient.post()
                    .uri("/api/notify/mobile")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of(
                            "username", username,
                            "title", title,
                            "message", message
                    ))
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(response ->
                                    System.out.println("📲 Fallback push sent: " + response),
                            error ->
                                    System.err.println("❌ Failed to send fallback: " + error.getMessage())
                    );

        } catch (Exception e) {
            System.err.println("⚠️ Failed to send FCM fallback: " + e.getMessage());
        }
    }
}
