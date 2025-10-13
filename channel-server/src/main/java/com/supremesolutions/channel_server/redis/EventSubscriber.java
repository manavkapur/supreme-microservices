package com.supremesolutions.channel_server.redis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supremesolutions.channel_server.dto.ChannelMessage;
import com.supremesolutions.channel_server.service.ChannelService;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class EventSubscriber {

    private final ReactiveRedisMessageListenerContainer listenerContainer;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ChannelService channelService;

    public EventSubscriber(ReactiveRedisConnectionFactory factory, ChannelService channelService) {
        this.listenerContainer = new ReactiveRedisMessageListenerContainer(factory);
        this.channelService = channelService;
    }

    @PostConstruct
    public void subscribe() {
        Flux.from(listenerContainer.receive(
                        new ChannelTopic("quote-updates"),
                        new ChannelTopic("contact-updates")
                ))
                .map(msg -> msg.getMessage())
                .subscribe(this::handleMessage);
    }

    private void handleMessage(String payload) {
        try {
            JsonNode node = mapper.readTree(payload);
            String userId = node.path("userId").asText(null);
            if (userId != null) {
                ChannelMessage message = new ChannelMessage(
                        "system",
                        userId,
                        node.toString(),
                        System.currentTimeMillis()
                );
                channelService.onMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
