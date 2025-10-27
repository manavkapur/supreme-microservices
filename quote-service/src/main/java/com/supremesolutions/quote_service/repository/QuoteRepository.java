package com.supremesolutions.quote_service.repository;

import com.supremesolutions.quote_service.entity.Quote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    List<Quote> findByEmail(String email);
    Page<Quote> findByStatus(String status, Pageable pageable);
}
