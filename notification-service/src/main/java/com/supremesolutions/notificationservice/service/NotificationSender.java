package com.supremesolutions.notificationservice.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.supremesolutions.notificationservice.dto.NotificationEvent;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class NotificationSender {

    private final UserClient userClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public NotificationSender(UserClient userClient) {
        this.userClient = userClient;
    }

    public void handleEvent(NotificationEvent ev) {
        userClient.fetchFcmToken(ev.getUserId())
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(token -> {
                    if (token == null || token.isEmpty()) {
                        System.out.println("⚠️ No FCM token for userId=" + ev.getUserId());
                        return;
                    }
                    sendPush(token, ev.getTitle(), ev.getBody(), ev.getPayload());
                }, Throwable::printStackTrace);
    }

    private void sendPush(String token, String title, String body, Object payload) {
        try {
            Notification notification = Notification.builder()
                    .setTitle(title != null ? title : "Update")
                    .setBody(body != null ? body : "")
                    .build();

            Message.Builder messageBuilder = Message.builder()
                    .setToken(token)
                    .setNotification(notification);

            if (payload != null) {
                messageBuilder.putData("payload", mapper.writeValueAsString(payload));
            }

            Message message = messageBuilder.build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("✅ Sent FCM message: " + response);
        } catch (Exception e) {
            System.err.println("❌ FCM send error:");
            e.printStackTrace();
        }
    }
}
