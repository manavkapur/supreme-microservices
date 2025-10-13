package com.supremesolutions.channel_server.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supremesolutions.channel_server.dto.ChannelMessage;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationPublisher {

    private final ReactiveStringRedisTemplate redis;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String channelName = "notification-events";

    public NotificationPublisher(ReactiveStringRedisTemplate redis) {
        this.redis = redis;
    }

    public void publishNotificationEvent(ChannelMessage msg) {
        try {
            String payload = mapper.writeValueAsString(msg);
            redis.convertAndSend(channelName, payload).subscribe();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
