package com.supremesolutions.contact.service;

import com.supremesolutions.contact.model.ContactMessage;
import com.supremesolutions.contact.repository.ContactRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactService {

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public ContactMessage save(ContactMessage contactMessage) {
        return contactRepository.save(contactMessage);
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
