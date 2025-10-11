package com.supremesolutions.quote_service.service;

import com.supremesolutions.quote_service.model.Quote;
import com.supremesolutions.quote_service.repository.QuoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuoteService {

    private final QuoteRepository quoteRepository;

    public QuoteService(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }

    // Get all quotes
    public List<Quote> getAllQuotes() {
        return quoteRepository.findAll();
    }

    // Add a new quote
    public Quote addQuote(Quote quote) {
        return quoteRepository.save(quote);
    }

    // Get quote by ID
    public Optional<Quote> getQuoteById(Long id) {
        return quoteRepository.findById(id);
    }

    // Update existing quote
    public Quote updateQuote(Long id, Quote updatedQuote) {
        return quoteRepository.findById(id)
                .map(quote -> {
                    quote.setAuthor(updatedQuote.getAuthor());
                    quote.setText(updatedQuote.getText());
                    return quoteRepository.save(quote);
                })
                .orElseThrow(() -> new RuntimeException("Quote not found with id " + id));
    }

    // Delete quote by ID
    public void deleteQuote(Long id) {
        quoteRepository.deleteById(id);
    }
}
