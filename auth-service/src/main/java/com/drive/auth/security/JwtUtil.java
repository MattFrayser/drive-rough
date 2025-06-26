// ============================================================================
// JWT Logic for stateless auth
// Uses: Sign tokens w/ HMAC SHA-256, Validate incoming tokens
// Claims: userID, publicKey
// ============================================================================
package com.drive.auth.security;

import com.drive.auth.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {
    // Generate a secure key for HS512
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private static final long EXPIRATION = 86400000; // 1 Day (fixed typo: was 8640000)

    public static String generateToken(User user) {
        return Jwts.builder() 
            .setSubject(user.getUsername())
            .claim("userId", user.getId().toString())
            .claim("publicKey", user.getPublicKey())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
            .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
            .compact();
    }

    public static Jws<Claims> validateToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(SECRET_KEY)
            .build()
            .parseClaimsJws(token);
    }
}
