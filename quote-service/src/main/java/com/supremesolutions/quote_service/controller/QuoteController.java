package com.supremesolutions.quote_service.controller;

import com.supremesolutions.quote_service.model.Quote;
import com.supremesolutions.quote_service.service.QuoteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quote")
public class QuoteController {

    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping
    public List<Quote> getAllQuotes() {
        return quoteService.getAllQuotes();
    }

    @GetMapping("/{id}")
    public Quote getQuoteById(@PathVariable Long id) {
        return quoteService.getQuoteById(id)
                .orElseThrow(() -> new RuntimeException("Quote not found with id " + id));
    }

    @PostMapping
    public Quote addQuote(@RequestBody Quote quote) {
        return quoteService.addQuote(quote);
    }

    @PutMapping("/{id}")
    public Quote updateQuote(@PathVariable Long id, @RequestBody Quote updatedQuote) {
        return quoteService.updateQuote(id, updatedQuote);
    }

    @DeleteMapping("/{id}")
    public void deleteQuote(@PathVariable Long id) {
        quoteService.deleteQuote(id);
    }
}
