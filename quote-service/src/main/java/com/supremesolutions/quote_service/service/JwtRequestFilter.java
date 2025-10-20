package com.supremesolutions.quote_service.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class JwtRequestFilter implements Filter {

    @Autowired
    private JwtService jwtService;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtService.validateToken(token)) {
                String username = jwtService.extractUsername(token);
                request.setAttribute("username", username);
            } else {
                request.setAttribute("username", null);
            }
        } else {
            request.setAttribute("username", null);
        }

        chain.doFilter(req, res);
    }
}
