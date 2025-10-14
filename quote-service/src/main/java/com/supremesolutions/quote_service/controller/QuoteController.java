package com.supremesolutions.quote_service.controller;

import com.supremesolutions.quote_service.entity.Quote;
import com.supremesolutions.quote_service.service.QuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    @PostMapping("/create")
    public ResponseEntity<Quote> createQuote(@RequestBody Quote quote) {
        return ResponseEntity.ok(quoteService.createQuote(quote));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Quote> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(quoteService.updateStatus(id, status));
    }
}
