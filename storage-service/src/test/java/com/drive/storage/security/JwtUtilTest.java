// ============================================================================
// JWT Token Generator for Testing
// ============================================================================

package com.drive.storage.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

public class JwtUtilTest {
    
    // Use the same key as JwtUtil for consistency
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    
    public static String generateToken(UUID userId, String username) {
        return generateToken(userId, username, 3600000); // 1 hour default
    }
    
    public static String generateToken(UUID userId, String username, long expirationMs) {
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
    
    public static String generateExpiredToken(UUID userId, String username) {
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
    
    public static String generateInvalidToken() {
        return "invalid.jwt.token";
    }
}
