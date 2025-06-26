package com.drive.common.encryption;

import java.time.Instant;
import java.util.UUID;

public class FilePermission {

    private UUID id;
    private UUID fileId;
    private UUID userId;
    private PermissionType permission;
    private String encryptedAccessKey;
    private Instant grantedAt;
    private Instant expiresAt;

    public FilePermission() {
        this.id = UUID.randomUUID();
        this.grantedAt = Instant.now();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getFileId() { return fileId; }
    public void setFileId(UUID fileId) { this.fileId = fileId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public PermissionType getPermission() { return permission; }
    public void setPermission(PermissionType permission) { this.permission = permission; }

    public String getEncryptedAccessKey() { return encryptedAccessKey; }
    public void setEncryptedAccessKey(String encryptedAccessKey) { this.encryptedAccessKey = encryptedAccessKey; }

    public Instant getGrantedAt() { return grantedAt; }
    public void setGrantedAt(Instant grantedAt) { this.grantedAt = grantedAt; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public enum PermissionType {
        READ, WRITE, ADMIN
    }
}

