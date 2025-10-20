package com.supremesolutions.contact.service;

import com.supremesolutions.contact.dto.ContactEvent;
import com.supremesolutions.contact.entity.Contact;
import com.supremesolutions.contact.redis.RedisPublisher;
import com.supremesolutions.contact.repository.ContactRepository;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class ContactService {

    private final ContactRepository repository;
    private final RedisPublisher redisPublisher;

    public ContactService(ContactRepository repository, RedisPublisher redisPublisher) {
        this.repository = repository;
        this.redisPublisher = redisPublisher;
    }

    public Contact saveContact(Contact contact, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");

        if (username != null) {
            contact.setUserId(username);
            System.out.println("📩 Contact submitted by logged-in user: " + username);
        } else {
            contact.setUserId("guest");
            System.out.println("📩 Contact submitted by guest user");
        }

        Contact saved = repository.save(contact);

        ContactEvent event = new ContactEvent(
                "contact.received",
                String.valueOf(saved.getId()),
                saved.getName(),
                saved.getEmail(),
                saved.getMessage(),
                username != null ? username : "guest" // 👈 Added here
        );

        redisPublisher.publish("contact-updates", event);
        return saved;
    }
}
