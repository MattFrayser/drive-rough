package com.drive.collab.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;

import javax.crypto.SecretKey;
import java.util.Map;

@Component
public class JwtWebSocketInterceptor implements HandshakeInterceptor {

    @Value("${jwt.secret:defaultSecretKey}")
    private String jwtSecret;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   org.springframework.web.socket.WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            String token = servletRequest.getServletRequest().getParameter("token");
            
            if (token != null && validateToken(token)) {
                // Extract user info from token and store in attributes
                Claims claims = parseToken(token);
                attributes.put("userId", claims.getSubject());
                attributes.put("userEmail", claims.get("email", String.class));
                return true;
            }
        }
        
        return false; // Reject connection if no valid token
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                              org.springframework.web.socket.WebSocketHandler wsHandler,
                              Exception exception) {
        // Post-handshake logic if needed
    }

    private boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
} 