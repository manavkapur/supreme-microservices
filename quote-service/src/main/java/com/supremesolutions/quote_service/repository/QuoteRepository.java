package com.supremesolutions.quote_service.repository;

import com.supremesolutions.quote_service.model.Quote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuoteRepository extends JpaRepository<Quote, Long> {
}
