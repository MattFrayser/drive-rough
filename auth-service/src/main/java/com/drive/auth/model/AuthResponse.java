// ============================================================================
// JWT token and public publicKey
// ============================================================================
package com.drive.auth.model;

public class AuthResponse {
    public String token;
    public String publicKey;

    public AuthResponse(String token, String publicKey) throws Exception {
        this.token = token;
        this.publicKey = publicKey;
    }
}
