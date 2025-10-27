package com.supremesolutions.contact.controller;

import com.supremesolutions.contact.entity.Contact;
import com.supremesolutions.contact.service.ContactService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactService service;

    public ContactController(ContactService service) {
        this.service = service;
    }

    // ✅ Normal user submission
    @PostMapping
    public ResponseEntity<Contact> submitContact(@RequestBody Contact contact, HttpServletRequest request) {
        Contact saved = service.saveContact(contact, request);
        return ResponseEntity.ok(saved);
    }

    // ✅ ADMIN: Get all unresolved (paginated)
    @GetMapping("/admin/unresolved")
    public ResponseEntity<Page<Contact>> getUnresolved(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(service.getUnresolvedContacts(pageable));
    }

    // ✅ ADMIN: Mark as resolved
    @PutMapping("/admin/{id}/resolve")
    public ResponseEntity<?> resolveContact(@PathVariable Long id, HttpServletRequest request) {
        String admin = (String) request.getAttribute("username");
        service.resolveContact(id, admin);
        return ResponseEntity.ok(Map.of("message", "Contact #" + id + " marked resolved"));
    }
}