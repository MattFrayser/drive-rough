// ============================================================================
// File Sharing Response
// =============================================================================

package com.drive.common.api;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable DTO for file sharing responses.
 */
public class ShareFileResponse {

    private final UUID fileId;
    private final UUID permissionId;
    private final UUID recipientId;
    private final PermissionType permission; // READ, WRITE, ADMIN
    private final String encryptedAccessKey;
    private final Instant grantedAt;
    private final Instant expiresAt;

    public ShareFileResponse(UUID fileId, UUID permissionId, UUID recipientId,
                                PermissionType permission, String encryptedAccessKey,
                                Instant grantedAt, Instant expiresAt) {
        this.fileId = fileId;
        this.permissionId = permissionId;
        this.recipientId = recipientId;
        this.permission = permission;
        this.encryptedAccessKey = encryptedAccessKey;
        this.grantedAt = grantedAt;
        this.expiresAt = expiresAt;
    }

    // getters
    public UUID getFileId() { return fileId; }
    public UUID getPermissionId() { return permissionId; }
    public UUID getRecipientId() { return recipientId; }
    public PermissionType getPermission() { return permission; }
    public String getEncryptedAccessKey() { return encryptedAccessKey; }
    public Instant getGrantedAt() { return grantedAt; }
    public Instant getExpiresAt() { return expiresAt; }

    public enum PermissionType {
        READ, WRITE, ADMIN
    }
}

