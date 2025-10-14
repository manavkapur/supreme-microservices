package com.supremesolutions.channel_server.config;

import com.supremesolutions.channel_server.listener.RedisEventSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisSubscriberConfig {

    @Bean
    RedisMessageListenerContainer redisContainer(
            RedisConnectionFactory connectionFactory,
            RedisEventSubscriber subscriber) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // Subscribe to core event channels
        container.addMessageListener(subscriber, new ChannelTopic("contact-updates"));
        container.addMessageListener(subscriber, new ChannelTopic("quote-events"));

        System.out.println("✅ Subscribed to [contact-updates, quote-updates]");
        return container;
    }

    @Bean
    public ChannelTopic notificationTopic() {
        return new ChannelTopic("notification-events");
    }
}
