package com.supremesolutions.quote_service;

import com.supremesolutions.quote_service.model.Quote;
import com.supremesolutions.quote_service.repository.QuoteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class QuoteServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuoteServiceApplication.class, args);
    }

    // Preload data when the app starts
    @Bean
    CommandLineRunner loadData(QuoteRepository quoteRepository) {
        return args -> {
            quoteRepository.save(new Quote("Steve Jobs", "Stay hungry, stay foolish."));
            quoteRepository.save(new Quote("Elon Musk", "When something is important enough, you do it even if the odds are not in your favor."));
            quoteRepository.save(new Quote("Albert Einstein", "Imagination is more important than knowledge."));
            quoteRepository.save(new Quote("Manav Kapur", "Work smart, train hard, stay consistent."));
            quoteRepository.save(new Quote("Tony Stark", "Sometimes you gotta run before you can walk."));

            System.out.println("✅ Sample quotes loaded successfully!");
        };
    }
}
