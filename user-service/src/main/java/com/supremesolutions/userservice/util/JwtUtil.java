package com.supremesolutions.userservice.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.Map;

public class JwtUtil {
    private static final String SECRET_KEY = "supreme-solutions-secret-key-9876543210@#jwt"; // keep readable
    private static final long EXPIRATION_MS = 86400000; // 1 day

    public static String generateToken(String email, String role, Long userId) {
        return Jwts.builder()
                .setSubject(email)
                .addClaims(Map.of(
                        "role", role,
                        "userId", userId
                ))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes()) // ✅ fixed here
                .compact();
    }

    public static String extractEmail(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes()) // ✅ also fix parser
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public static String extractRole(String token) {
        return (String) Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes())
                .parseClaimsJws(token)
                .getBody()
                .get("role");
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
