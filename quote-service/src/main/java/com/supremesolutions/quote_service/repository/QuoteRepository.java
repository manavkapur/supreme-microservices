package com.supremesolutions.quote_service.repository;

import com.supremesolutions.quote_service.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    // ✅ Fetch all quotes submitted by a user (using their email)
    List<Quote> findByEmail(String email);
}
