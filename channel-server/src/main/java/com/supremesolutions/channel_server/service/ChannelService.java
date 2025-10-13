package com.supremesolutions.channel_server.service;

import com.supremesolutions.channel_server.dto.ChannelMessage;
import com.supremesolutions.channel_server.redis.NotificationPublisher;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

@Service
public class ChannelService {

    private final ConcurrentHashMap<String, Set<String>> presence = new ConcurrentHashMap<>();
    private final NotificationPublisher publisher;

    public ChannelService(NotificationPublisher publisher) {
        this.publisher = publisher;
    }

    public boolean isOnline(String userEmail) {
        return presence.containsKey(userEmail);
    }

    public void onMessage(ChannelMessage msg) {
        // publish notification event to Redis
        publisher.publishNotificationEvent(msg);
    }
}
