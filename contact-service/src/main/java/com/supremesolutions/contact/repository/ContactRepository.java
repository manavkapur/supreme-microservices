package com.supremesolutions.contact.repository;

import com.supremesolutions.contact.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    Page<Contact> findByStatus(String status, Pageable pageable);
}
