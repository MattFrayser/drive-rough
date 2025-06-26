// ============================================================================
// Encrypted File Information
// =============================================================================

package com.drive.common.api;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable DTO for encrypted file information.
 */
public class EncryptedFileInfo {
    private final UUID id;
    private final UUID ownerId;
    private final String encryptedFileName;
    private final long encryptedSize;
    private final long originalSize;
    private final String algorithm;
    private final String keyId;
    private final Instant createdAt;
    private final Instant updatedAt;

    public EncryptedFileInfo(UUID id, UUID ownerId, String encryptedFileName, long encryptedSize, long originalSize, String algorithm, String keyId, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.encryptedFileName = encryptedFileName;
        this.encryptedSize = encryptedSize;
        this.originalSize = originalSize;
        this.algorithm = algorithm;
        this.keyId = keyId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public UUID getOwnerId() { return ownerId; }
    public String getEncryptedFileName() { return encryptedFileName; }
    public long getEncryptedSize() { return encryptedSize; }
    public long getOriginalSize() { return originalSize; }
    public String getAlgorithm() { return algorithm; }
    public String getKeyId() { return keyId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
