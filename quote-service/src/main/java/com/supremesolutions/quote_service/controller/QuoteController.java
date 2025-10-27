package com.supremesolutions.quote_service.controller;

import com.supremesolutions.quote_service.entity.Quote;
import com.supremesolutions.quote_service.repository.QuoteRepository;
import com.supremesolutions.quote_service.service.QuoteService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;
    private final QuoteRepository quoteRepository;

    // ✅ Create quote (user)
    @PostMapping("/create")
    public ResponseEntity<Quote> createQuote(@RequestBody Quote quote, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        return ResponseEntity.ok(quoteService.createQuote(quote, username));
    }

    // ✅ Update status (user or admin)
    @PutMapping("/{id}/status")
    public ResponseEntity<Quote> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        return ResponseEntity.ok(quoteService.updateStatus(id, status, username));
    }

    // ✅ Fetch user’s own quotes
    @GetMapping("/user/{email}")
    public ResponseEntity<List<Quote>> getQuotesByUser(@PathVariable String email) {
        List<Quote> quotes = quoteRepository.findByEmail(email.toLowerCase());
        return ResponseEntity.ok(quotes);
    }

    // ✅ Admin-only: fetch pending quotes (paged)
    @GetMapping("/admin/pending")
    public ResponseEntity<Page<Quote>> getPendingQuotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Quote> pending = quoteRepository.findByStatus("Pending", pageable);
        return ResponseEntity.ok(pending);
    }

    // ✅ Admin-only: approve/reject quote
    @PutMapping("/admin/{id}/status")
    public ResponseEntity<?> updateQuoteStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {

        String username = (String) request.getAttribute("username");
        String newStatus = body.get("status");

        quoteService.updateStatus(id, newStatus, username);

        return ResponseEntity.ok(Map.of(
                "message", "Quote #" + id + " updated to " + newStatus
        ));
    }
}
