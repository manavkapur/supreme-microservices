package com.supremesolutions.channel_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ✅ New syntax for disabling CSRF in Spring Security 6.3+
                .csrf(csrf -> csrf.disable())
                // ✅ New request authorization DSL
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ws/**", "/actuator/**").permitAll()
                        .anyRequest().permitAll()
                )
                // optional: disable default login form if not needed
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
