package com.drive.storage.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

/**
 * Custom authentication token for JWT-based authentication
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String username;
    private final UUID userId;
    private final Object credentials;

    public JwtAuthenticationToken(String username, UUID userId, Object credentials) {
        super(null);
        this.username = username;
        this.userId = userId;
        this.credentials = credentials;
        setAuthenticated(true);
    }

    public JwtAuthenticationToken(String username, UUID userId, Object credentials, 
                                 Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.username = username;
        this.userId = userId;
        this.credentials = credentials;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

    @Override
    public String getName() {
        return username;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}
