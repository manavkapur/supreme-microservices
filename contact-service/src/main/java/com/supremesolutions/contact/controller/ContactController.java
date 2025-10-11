package com.supremesolutions.contact.controller;

import com.supremesolutions.contact.model.ContactMessage;
import com.supremesolutions.contact.service.ContactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = "http://localhost:3000")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    // Create new message
    @PostMapping
    public ResponseEntity<Map<String, Object>> createMessage(@Valid @RequestBody ContactMessage contactMessage) {
        ContactMessage saved = contactService.save(contactMessage);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Message submitted successfully");
        response.put("data", saved);

        return ResponseEntity.ok(response);
    }

    // Fetch all
    @GetMapping
    public ResponseEntity<List<ContactMessage>> getAllMessages() {
        return ResponseEntity.ok(contactService.findAll());
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMessage(@PathVariable Long id) {
        if (!contactService.exists(id)) {
            return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", "Message not found with id " + id
            ));
        }

        contactService.deleteById(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Message deleted successfully"
        ));
    }

    // Archive
    @PutMapping("/{id}/archive")
    public ResponseEntity<Map<String, Object>> archiveMessage(@PathVariable Long id) {
        return contactService.findById(id).map(msg -> {
            msg.setArchived(true);
            contactService.save(msg);

            Map<String, Object> body = new HashMap<>();
            body.put("success", true);
            body.put("message", "Message archived");

            return ResponseEntity.ok(body);
        }).orElseGet(() -> ResponseEntity.status(404).body(Map.of(
                "success", false,
                "message", "Message not found"
        )));
    }

    // Active messages
    @GetMapping("/active")
    public List<ContactMessage> getActiveMessages() {
        return contactService.getActiveMessages();
    }

    // Archived messages
    @GetMapping("/archived")
    public List<ContactMessage> getArchivedMessages() {
        return contactService.getArchivedMessages();
    }
}
