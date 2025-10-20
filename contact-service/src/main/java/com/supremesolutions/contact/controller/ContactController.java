package com.supremesolutions.contact.controller;

import com.supremesolutions.contact.entity.Contact;
import com.supremesolutions.contact.service.ContactService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactService service;

    public ContactController(ContactService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Contact> submitContact(@RequestBody Contact contact, HttpServletRequest request) {
        Contact saved = service.saveContact(contact, request);
        return ResponseEntity.ok(saved);
    }
}
