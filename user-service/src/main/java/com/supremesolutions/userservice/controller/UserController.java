package com.supremesolutions.userservice.controller;

import com.supremesolutions.userservice.entity.User;
import com.supremesolutions.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.supremesolutions.userservice.util.JwtUtil;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.saveUser(user));
    }

    @PostMapping("/register-fcm")
    public ResponseEntity<?> registerFcm(@RequestBody Map<String, String> body) {
        Long userId = Long.parseLong(body.get("userId"));
        String fcmToken = body.get("fcmToken");

        boolean updated = userService.registerFcmToken(userId, fcmToken);
        if (updated) {
            return ResponseEntity.ok(Map.of("status", "success", "message", "FCM token registered"));
        } else {
            return ResponseEntity.status(404).body(Map.of("status", "error", "message", "User not found"));
        }
    }

    @GetMapping("/fcm-token/{userId}")
    public ResponseEntity<?> getFcmToken(@PathVariable Long userId) {
        return userService.getFcmToken(userId)
                .<ResponseEntity<?>>map(token -> ResponseEntity.ok(Map.of("fcmToken", token)))
                .orElse(ResponseEntity.status(404).body(Map.of("error", "User not found")));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
        }

        if (!userService.matchesPassword(password, user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
        }

        String token = JwtUtil.generateToken(email);
        return ResponseEntity.ok(Map.of("token", token, "userId", user.getId(), "email", user.getEmail()));
    }

    @GetMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing token"));
        }

        String token = authHeader.substring(7);
        if (JwtUtil.validateToken(token)) {
            String email = JwtUtil.extractEmail(token);
            return ResponseEntity.ok(Map.of("valid", true, "email", email));
        } else {
            return ResponseEntity.status(401).body(Map.of("valid", false, "error", "Invalid or expired token"));
        }
    }

    @GetMapping("/public-key")
    public ResponseEntity<?> getPublicKey() {
        return ResponseEntity.ok(Map.of("key", "supreme-solutions-secret-key-9876543210@#jwt"));
    }

}
