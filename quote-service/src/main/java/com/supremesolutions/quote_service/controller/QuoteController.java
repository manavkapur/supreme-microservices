package com.supremesolutions.quote_service.controller;

import com.supremesolutions.quote_service.entity.Quote;
import com.supremesolutions.quote_service.service.QuoteService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    @PostMapping("/create")
    public ResponseEntity<Quote> createQuote(@RequestBody Quote quote, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        return ResponseEntity.ok(quoteService.createQuote(quote, username));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Quote> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        return ResponseEntity.ok(quoteService.updateStatus(id, status, username));
    }
}
