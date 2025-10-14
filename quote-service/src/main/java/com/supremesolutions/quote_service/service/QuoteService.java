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

    public Quote createQuote(Quote quote) {
        quote.setStatus("Pending");
        Quote saved = quoteRepository.save(quote);

        Map<String, Object> event = new HashMap<>();
        event.put("event", "quote.created");
        event.put("quoteId", saved.getId());
        event.put("name", saved.getName());
        event.put("email", saved.getEmail());
        event.put("message", saved.getMessage());
        redisTemplate.convertAndSend("quote-events", event);

        log.info("Published quote.created event: {}", event);
        return saved;
    }

    public Quote updateStatus(Long id, String status) {
        Quote quote = quoteRepository.findById(id).orElseThrow();
        quote.setStatus(status);
        Quote updated = quoteRepository.save(quote);

        Map<String, Object> event = new HashMap<>();
        event.put("event", "quote.updated");
        event.put("quoteId", updated.getId());
        event.put("email", updated.getEmail());
        event.put("status", updated.getStatus());
        redisTemplate.convertAndSend("quote-events", event);

        log.info("Published quote.updated event: {}", event);
        return updated;
    }
}
