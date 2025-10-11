package com.supremesolutions.quote_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String author;
    private String text;

    public Quote() {}

    public Quote(String author, String text) {
        this.author = author;
        this.text = text;
    }

    public Long getId() { return id; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
