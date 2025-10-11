package com.supremesolutions.api_gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
public class FallbackController {

    @GetMapping("/fallback/global")
    public ResponseEntity<Map<String, String>> globalFallback() {
        return ResponseEntity
                .ok(Map.of(
                        "message", "Service is temporarily unavailable. Please try again later.",
                        "status", "fallback"
                ));
    }
}
