// ============================================================================
// Encryption Error Response
// =============================================================================

package com.drive.common.api;

import java.time.Instant;

/**
 * Immutable DTO for encryption error responses.
 */
public class EncryptionErrorResponse {
    
    private final String error;
    private final String code;
    private final String message;
    private final Instant timestamp;

    public EncryptionErrorResponse(String error, String code, String message) {
        this.error = error;
        this.code = code;
        this.message = message;
        this.timestamp = Instant.now();
    }

    
    // getters
    public String getError() { return error; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
    public Instant getTimestamp() { return timestamp; }
}
