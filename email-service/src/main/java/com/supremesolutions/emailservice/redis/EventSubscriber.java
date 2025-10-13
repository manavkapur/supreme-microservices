package com.supremesolutions.emailservice.redis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supremesolutions.emailservice.dto.ContactEvent;
import com.supremesolutions.emailservice.dto.QuoteEvent;
import com.supremesolutions.emailservice.service.EmailSender;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class EventSubscriber {

    private final ReactiveRedisMessageListenerContainer listenerContainer;
    private final EmailSender sender;
    private final ObjectMapper mapper = new ObjectMapper();

    public EventSubscriber(ReactiveRedisConnectionFactory factory, EmailSender sender) {
        this.listenerContainer = new ReactiveRedisMessageListenerContainer(factory);
        this.sender = sender;
    }

    @PostConstruct
    public void subscribe() {
        Flux.from(listenerContainer.receive(new ChannelTopic("contact-updates"),
                        new ChannelTopic("quote-updates")))
                .map(msg -> msg.getMessage())
                .subscribe(this::handle);
    }

    private void handle(String payload) {
        try {
            JsonNode node = mapper.readTree(payload);
            String eventType = node.path("event").asText("");

            if (eventType.startsWith("contact")) {
                ContactEvent ev = mapper.treeToValue(node, ContactEvent.class);
                sender.sendAdminNotification(ev);
            } else if (eventType.startsWith("quote")) {
                QuoteEvent ev = mapper.treeToValue(node, QuoteEvent.class);
                sender.sendQuoteUpdate(ev);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
