package com.supremesolutions.notificationservice.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supremesolutions.notificationservice.dto.NotificationEvent;
import com.supremesolutions.notificationservice.service.NotificationSender;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class EventSubscriber {

    private final ReactiveRedisMessageListenerContainer listenerContainer;
    private final NotificationSender sender;
    private final ObjectMapper mapper = new ObjectMapper();

    public EventSubscriber(ReactiveRedisConnectionFactory factory,
                           NotificationSender sender) {
        this.listenerContainer = new ReactiveRedisMessageListenerContainer(factory);
        this.sender = sender;
    }

    @PostConstruct
    public void start() {
        // subscribe to both notification-events and (optionally) contact/quote channels
        Flux.from(listenerContainer.receive(new ChannelTopic("notification-events"),
                        new ChannelTopic("quote-updates"),
                        new ChannelTopic("contact-updates")))
                .map(message -> message.getMessage())
                .subscribe(this::handle);
    }

    private void handle(String payload) {
        try {
            NotificationEvent ev = mapper.readValue(payload, NotificationEvent.class);

            // defensive: only process when userId is present
            if (ev.getUserId() == null) {
                // optionally log or route to admin email
                return;
            }

            sender.handleEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
