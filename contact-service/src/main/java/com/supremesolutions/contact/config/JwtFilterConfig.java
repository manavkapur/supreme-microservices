package com.supremesolutions.contact.config;

import com.supremesolutions.contact.security.JwtRequestFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtFilterConfig {

    @Bean
    public FilterRegistrationBean<JwtRequestFilter> jwtFilter(JwtRequestFilter filter) {
        FilterRegistrationBean<JwtRequestFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(filter);
        bean.addUrlPatterns("/api/*");
        return bean;
    }
}
