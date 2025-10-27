package com.supremesolutions.quote_service.service;

import com.supremesolutions.quote_service.entity.Quote;
import com.supremesolutions.quote_service.repository.QuoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public Quote createQuote(Quote quote, String username) {
        if (username != null)
            username = username.toLowerCase();

        quote.setStatus("Pending");
        quote.setEmail(quote.getEmail() != null ? quote.getEmail().toLowerCase() : null);

        Quote saved = quoteRepository.save(quote);

        // Publish new quote event
        publishEvent("quote.created", saved, username);

        return saved;
    }

    public Quote updateStatus(Long id, String status, String username) {
        if (username != null)
            username = username.toLowerCase();

        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quote not found"));

        quote.setStatus(status);
        Quote updated = quoteRepository.save(quote);

        // ✅ Use quote.getEmail() instead of admin username
        publishEvent("quote.updated", updated, quote.getEmail());
        return updated;
    }

    private void publishEvent(String eventType, Quote quote, String username) {
        Map<String, Object> event = new HashMap<>();
        event.put("event", eventType);
        event.put("quoteId", quote.getId());
        event.put("name", quote.getName());
        event.put("email", quote.getEmail());
        event.put("message", quote.getMessage());
        event.put("status", quote.getStatus());
        event.put("username", quote.getEmail()); // 👈 Force username = quote owner
        event.put("admin", username);            // 👈 Store who performed the action (optional)
        event.put("source", "quote-events");
        event.put("timestamp", System.currentTimeMillis());

        redisTemplate.convertAndSend("quote-events", event);
        log.info("📢 Published Redis Event → {} : {}", eventType, event);
    }

}
