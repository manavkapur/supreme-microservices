package com.supremesolutions.emailservice.config;

import com.supremesolutions.emailservice.listener.EmailEventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisConfig {

    @Bean
    RedisMessageListenerContainer container(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic("contact-events"));
        container.addMessageListener(listenerAdapter, new PatternTopic("contact-updates")); // ✅ Add this
        container.addMessageListener(listenerAdapter, new PatternTopic("quote-events"));
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(EmailEventListener listener) {
        return new MessageListenerAdapter(listener, "onMessage");
    }
}
