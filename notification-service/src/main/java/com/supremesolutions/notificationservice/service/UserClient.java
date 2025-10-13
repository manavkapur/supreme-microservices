package com.supremesolutions.notificationservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserClient {

    private final WebClient webClient;
    private final String userServiceBase;

    public UserClient(WebClient webClient,
                      @Value("${user-service.base-url:http://localhost:8084}") String userServiceBase) {
        this.webClient = webClient;
        this.userServiceBase = userServiceBase;
    }

    public Mono<String> fetchFcmToken(String userId) {
        // try endpoint: /api/users/{id}/fcm-token
        return webClient.get()
                .uri(userServiceBase + "/api/users/{id}/fcm-token", userId)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(node -> {
                    if (node.has("fcmToken")) return node.get("fcmToken").asText(null);
                    if (node.has("token")) return node.get("token").asText(null);
                    if (node.has("fcm_token")) return node.get("fcm_token").asText(null);
                    return null;
                })
                .onErrorReturn(null);
    }
}
