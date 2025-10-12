package com.supremesolutions.contact.service;

import com.supremesolutions.contact.model.ContactMessage;
import com.supremesolutions.contact.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ContactService {

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public ContactMessage save(ContactMessage contactMessage) {

        ContactMessage saved = contactRepository.save(contactMessage);

        Map<String, Object> event = new HashMap<>();
        event.put("type", "CONTACT_CREATED");
        event.put("contactId", saved.getId());
        event.put("name", saved.getName());
        event.put("email", saved.getEmail());
        event.put("message", saved.getMessage());

        redisTemplate.convertAndSend("contact-events", event);

        return saved;
    }

    public List<ContactMessage> findAll() {
        return contactRepository.findAll();
    }

    public Optional<ContactMessage> findById(Long id) {
        return contactRepository.findById(id);
    }

    public boolean exists(Long id) {
        return contactRepository.existsById(id);
    }

    public void deleteById(Long id) {
        contactRepository.deleteById(id);
    }

    public List<ContactMessage> getActiveMessages() {
        return contactRepository.findAll().stream()
                .filter(msg -> !msg.isArchived())
                .toList();
    }

    public List<ContactMessage> getArchivedMessages() {
        return contactRepository.findAll().stream()
                .filter(ContactMessage::isArchived)
                .toList();
    }
}
