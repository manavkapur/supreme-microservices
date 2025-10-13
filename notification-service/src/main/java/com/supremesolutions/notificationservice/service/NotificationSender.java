package com.supremesolutions.notificationservice.service;

import com.supremesolutions.notificationservice.dto.FcmMessage;
import com.supremesolutions.notificationservice.dto.NotificationEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.HashMap;

@Service
public class NotificationSender {

    private final UserClient userClient;
    private final WebClient webClient;
    private final String fcmServerKey;

    public NotificationSender(UserClient userClient, WebClient webClient,
                              @Value("${fcm.server-key:}") String fcmServerKey) {
        this.userClient = userClient;
        this.webClient = webClient;
        this.fcmServerKey = fcmServerKey;
    }

    public void handleEvent(NotificationEvent ev) {
        // fetch token
        userClient.fetchFcmToken(ev.getUserId())
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(token -> {
                    if (token == null || token.isEmpty()) {
                        // no token -> optionally store / log for later or send email
                        System.out.println("No FCM token for userId=" + ev.getUserId());
                        return;
                    }

                    sendPush(token, ev.getTitle(), ev.getBody(), ev.getPayload());
                }, err -> {
                    err.printStackTrace();
                });
    }

    private void sendPush(String token, String title, String body, Object payload) {
        if (fcmServerKey == null || fcmServerKey.isBlank()) {
            System.err.println("FCM server key not configured - cannot send push");
            return;
        }

        FcmMessage msg = new FcmMessage();
        msg.setTo(token);
        msg.setNotification(new FcmMessage.Notification(title != null ? title : "Update",
                body != null ? body : ""));
        // attach payload as data
        Map<String, Object> data = new HashMap<>();
        if (payload != null) data.put("payload", payload);
        msg.setData(data);

        webClient.post()
                .uri("https://fcm.googleapis.com/fcm/send")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "key=" + fcmServerKey)
                .bodyValue(msg)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(resp -> {
                    System.out.println("FCM resp: " + resp);
                }, err -> {
                    System.err.println("FCM error: ");
                    err.printStackTrace();
                });
    }
}
