// ============================================================================
// Encrypted Response for file upload
// =============================================================================

package com.drive.common.api;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable DTO for encrypted file upload responses.
 */
public class EncryptedUploadResponse {
    
    private final UUID fileId;
    private final String encryptedFileName;
    private final long encryptedSize;
    private final long originalSize;
    private final String algorithm;
    private final String keyId;
    private final Instant uploadedAt;

    public EncryptedUploadResponse(UUID fileId, String encryptedFileName, long encryptedSize,
                                    long originalSize, String algorithm, String keyId, Instant uploadedAt) {
        this.fileId = fileId;
        this.encryptedFileName = encryptedFileName;
        this.encryptedSize = encryptedSize;
        this.originalSize = originalSize;
        this.algorithm = algorithm;
        this.keyId = keyId;
        this.uploadedAt = uploadedAt;
    }

    // getters
    public UUID getFileId() { return fileId; }
    public String getEncryptedFileName() { return encryptedFileName; }
    public long getEncryptedSize() { return encryptedSize; }
    public long getOriginalSize() { return originalSize; }
    public String getAlgorithm() { return algorithm; }
    public String getKeyId() { return keyId; }
    public Instant getUploadedAt() { return uploadedAt; }
}
