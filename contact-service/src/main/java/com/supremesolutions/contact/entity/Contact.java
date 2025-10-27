package com.supremesolutions.contact.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String message;
    private String userId;

    // ✅ New fields
    private String status = "New";  // [New, Resolved]
    private Instant createdAt = Instant.now();

    // --- Getters ---
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getMessage() { return message; }
    public String getUserId() { return userId; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }

    // --- Setters ---
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setMessage(String message) { this.message = message; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
