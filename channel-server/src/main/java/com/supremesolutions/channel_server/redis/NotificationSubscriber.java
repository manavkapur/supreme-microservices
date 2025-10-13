package com.supremesolutions.channel_server.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supremesolutions.channel_server.dto.ChannelMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationSubscriber {

    private final ReactiveRedisMessageListenerContainer listenerContainer;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public NotificationSubscriber(ReactiveRedisMessageListenerContainer listenerContainer,
                                  SimpMessagingTemplate messagingTemplate) {
        this.listenerContainer = listenerContainer;
        this.messagingTemplate = messagingTemplate;

        listenerContainer.receive(ChannelTopic.of("notification-events"))
                .map(message -> message.getMessage())
                .subscribe(this::handleMessage);
    }

    private void handleMessage(String payload) {
        try {
            ChannelMessage msg = mapper.readValue(payload, ChannelMessage.class);
            log.info("Received Redis message: {}", msg);

            if (msg.targetUser() != null && !msg.targetUser().isBlank()) {
                // 🎯 Send to specific user
                messagingTemplate.convertAndSendToUser(
                        msg.targetUser(),
                        "/queue/notifications",
                        msg
                );
                log.info("Sent private notification to user: {}", msg.targetUser());
            } else {
                // 🌍 Broadcast to all
                messagingTemplate.convertAndSend("/topic/notifications", msg);
                log.info("Broadcasted notification to /topic/notifications");
            }

        } catch (Exception e) {
            log.error("Error processing Redis message: {}", e.getMessage());
        }
    }
}
