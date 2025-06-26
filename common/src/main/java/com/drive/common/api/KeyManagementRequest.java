// ============================================================================
// Key Management Request
// =============================================================================

package com.drive.common.api;

import java.util.UUID;

/**
 * Immutable DTO for key management requests.
 */
public class KeyManagementRequest {
    private final String operation; // ROTATE, REVOKE, GRANT_ACCESS
    private final UUID fileId;
    private final UUID userId;
    private final String newPublicKey;

    public KeyManagementRequest(String operation, UUID fileId, UUID userId, String newPublicKey) {
        this.operation = operation;
        this.fileId = fileId;
        this.userId = userId;
        this.newPublicKey = newPublicKey;
    }

    public String getOperation() { return operation; }
    public UUID getFileId() { return fileId; }
    public UUID getUserId() { return userId; }
    public String getNewPublicKey() { return newPublicKey; }
}
