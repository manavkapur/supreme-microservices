package com.supremesolutions.contact.service;

import com.supremesolutions.contact.dto.ContactEvent;
import com.supremesolutions.contact.entity.Contact;
import com.supremesolutions.contact.redis.RedisPublisher;
import com.supremesolutions.contact.repository.ContactRepository;
import org.springframework.stereotype.Service;

@Service
public class ContactService {

    private final ContactRepository repository;
    private final RedisPublisher redisPublisher;

    public ContactService(ContactRepository repository, RedisPublisher redisPublisher) {
        this.repository = repository;
        this.redisPublisher = redisPublisher;
    }

    public Contact saveContact(Contact contact) {
        Contact saved = repository.save(contact);

        // publish event to Redis
        ContactEvent event = new ContactEvent(
                "contact.received",
                String.valueOf(saved.getId()),
                saved.getName(),
                saved.getEmail(),
                saved.getMessage()
        );
        redisPublisher.publish("contact-updates", event);

        return saved;
    }
}
