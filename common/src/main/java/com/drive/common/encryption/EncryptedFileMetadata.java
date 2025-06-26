// ============================================================================
// Encrypt Sensitive FireMetadata, 
// Keep access controll and system metadata plaintext
//============================================================================

package com.drive.common.encryption;

import java.time.Instant;
import java.util.UUID;

public class EncryptedFileMetadata {

    // plaintext
    private UUID id;
    private UUID ownerId;
    private String StoragePath;
    private long encryptedSize;
    private Instant createdAt;
    private Instant updatedAt;

    // Encrypted 
    private String algorithm;
    private String keyId;
    private String encryptedChecksum;
    private String encryptedFilename;
    private String encryptedContentType;

    // Access control 
    private String acccessKeyEncrypted;

    public EncryptedFileMetadata() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOwnerId() { return ownerId; }
    public void setOwnerId(UUID ownerId) { this.ownerId = ownerId; }

    public String getStoragePath() { return StoragePath; }
    public void setStoragePath(String storagePath) { this.StoragePath = storagePath; }

    public long getEncryptedSize() { return encryptedSize; }
    public void setEncryptedSize(long encryptedSize) { this.encryptedSize = encryptedSize; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }

    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }

    public String getEncryptedChecksum() { return encryptedChecksum; }
    public void setEncryptedChecksum(String encryptedChecksum) { this.encryptedChecksum = encryptedChecksum; }

    public String getEncryptedFilename() { return encryptedFilename; }
    public void setEncryptedFilename(String encryptedFilename) { this.encryptedFilename = encryptedFilename; }

    public String getEncryptedContentType() { return encryptedContentType; }
    public void setEncryptedContentType(String encryptedContentType) { this.encryptedContentType = encryptedContentType; }

    public String getAcccessKeyEncrypted() { return acccessKeyEncrypted; }
    public void setAcccessKeyEncrypted(String acccessKeyEncrypted) { this.acccessKeyEncrypted = acccessKeyEncrypted; }

}
    
