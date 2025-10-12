package com.supremesolutions.userservice.controller;

import com.supremesolutions.userservice.entity.User;
import com.supremesolutions.userservice.repository.UserRepository;
import com.supremesolutions.userservice.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ✅ Register new user
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already registered!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    // ✅ Login existing user
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return ResponseEntity.ok().body(
                java.util.Map.of("token", token, "email", user.getEmail(), "role", user.getRole())
        );
    }

    // ✅ Get current logged-in user (requires JWT)
    @GetMapping("/me")
    public ResponseEntity<?> getMe(@RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        Optional<User> user = userRepository.findByEmail(email);
        return user.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body("User not found"));
    }

    // ✅ Get all users (admin only, for now no role enforcement)
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // ✅ Update FCM token
    @PostMapping("/update-fcm")
    public ResponseEntity<?> updateFcmToken(@RequestHeader("Authorization") String token,
                                            @RequestParam String fcmToken) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOpt.get();
        user.setFcmToken(fcmToken);
        userRepository.save(user);

        return ResponseEntity.ok("FCM token updated successfully");
    }
}
