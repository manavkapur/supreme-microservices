package com.supremesolutions.contact.service;

import com.supremesolutions.contact.dto.ContactEvent;
import com.supremesolutions.contact.entity.Contact;
import com.supremesolutions.contact.redis.RedisPublisher;
import com.supremesolutions.contact.repository.ContactRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@Service
public class ContactService {

    private final ContactRepository repository;
    private final RedisPublisher redisPublisher;

    public ContactService(ContactRepository repository, RedisPublisher redisPublisher) {
        this.repository = repository;
        this.redisPublisher = redisPublisher;
    }

    // ✅ User submits contact form
    public Contact saveContact(Contact contact, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");

        if (username != null) {
            contact.setUserId(username);
            System.out.println("📩 Contact submitted by logged-in user: " + username);
        } else {
            contact.setUserId("guest");
            System.out.println("📩 Contact submitted by guest user");
        }

        contact.setStatus("New");
        Contact saved = repository.save(contact);

        ContactEvent event = new ContactEvent(
                "contact.received",
                String.valueOf(saved.getId()),
                saved.getName(),
                saved.getEmail(),
                saved.getMessage(),
                username != null ? username : "guest"
        );

        redisPublisher.publish("contact-updates", event);
        return saved;
    }

    // ✅ Admin fetch unresolved
    public Page<Contact> getUnresolvedContacts(Pageable pageable) {
        return repository.findByStatus("New", pageable);
    }

    // ✅ Admin resolve contact
    public void resolveContact(Long id, String adminUsername) {
        Contact contact = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));
        contact.setStatus("Resolved");
        repository.save(contact);

        // Publish Redis event
        Map<String, Object> event = Map.of(
                "event", "contact.updated",
                "source", "contact-updates",
                "contactId", contact.getId(),
                "email", contact.getEmail(),
                "status", "Resolved",
                "admin", adminUsername != null ? adminUsername : "unknown",
                "timestamp", System.currentTimeMillis()
        );

        redisPublisher.publish("contact-updates", event);
    }
}