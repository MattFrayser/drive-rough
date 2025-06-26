package com.drive.storage.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

public class JwtTestUtil {
    
    // Use a consistent key for testing
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    
    public static String generateTestToken(UUID userId, String username) {
        return generateTestToken(userId, username, 3600000); // 1 hour default
    }
    
    public static String generateTestToken(UUID userId, String username, long expirationMs) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);
        
        return Jwts.builder()
            .setSubject(username)
            .claim("userId", userId.toString())
            .setIssuedAt(now)
            .setExpiration(expiration)
            .signWith(SECRET_KEY)
            .compact();
    }
    
    public static String generateExpiredTestToken(UUID userId, String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() - 3600000); // Expired 1 hour ago
        
        return Jwts.builder()
            .setSubject(username)
            .claim("userId", userId.toString())
            .setIssuedAt(new Date(now.getTime() - 7200000)) // Issued 2 hours ago
            .setExpiration(expiration)
            .signWith(SECRET_KEY)
            .compact();
    }
    
    public static String createAuthHeader(String token) {
        return "Bearer " + token;
    }
    
    public static String createAuthHeader(UUID userId, String username) {
        return createAuthHeader(generateTestToken(userId, username));
    }
}
