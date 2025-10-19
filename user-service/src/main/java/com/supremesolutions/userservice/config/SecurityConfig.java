package com.supremesolutions.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.disable()) // ❌ remove this later if needed
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/users/register",
                                "/api/users/login",
                                "/api/users/register-fcm",
                                "/api/users/fcm-token/**",
                                "/api/users/verify-token",
                                "/actuator/**"
                        ).permitAll()
                        .anyRequest().permitAll()
                )
                .httpBasic(httpBasic -> {});
        return http.build();
    }
}
