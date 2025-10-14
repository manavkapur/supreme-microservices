package com.supremesolutions.notificationservice.config;

import com.supremesolutions.notificationservice.listener.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisSubscriberConfig {

    @Bean
    public RedisMessageListenerContainer redisContainer(
            RedisConnectionFactory connectionFactory,
            NotificationEventSubscriber subscriber) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // Subscribes only to notification-events
        container.addMessageListener(subscriber, new ChannelTopic("notification-events"));

        System.out.println("✅ Subscribed to [notification-events]");
        return container;
    }
}
