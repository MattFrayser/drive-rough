// ============================================================================
// File Sharing Request
// =============================================================================

package com.drive.common.api;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable DTO for file sharing requests.
 */
public class ShareFileRequest {
    private final UUID fileId;
    private final UUID userId;
    private final PermissionType permission;
    private final Instant expiresAt;

    public ShareFileRequest(UUID fileId, UUID userId, PermissionType permission, Instant expiresAt) {
        this.fileId = fileId;
        this.userId = userId;
        this.permission = permission;
        this.expiresAt = expiresAt;
    }

    public UUID getFileId() { return fileId; }
    public UUID getUserId() { return userId; }
    public PermissionType getPermission() { return permission; }
    public Instant getExpiresAt() { return expiresAt; }

    public enum PermissionType {
        READ, WRITE, ADMIN
    }
}
