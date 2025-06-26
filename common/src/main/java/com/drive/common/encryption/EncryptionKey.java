package com.drive.common.encryption;

import java.time.Instant;

public class EncryptionKey {

    private String keyId;
    private String algorithm;
    private byte[] keyData;
    private Instant createdAt;
    private Instant expiresAt;

    public EncryptionKey(String keyId, String algorithm, byte[] keyData) {
        this.keyId = keyId;
        this.algorithm = algorithm;
        this.keyData = keyData;
        this.createdAt = Instant.now();
    }

    // Getters
    public String getKeyId() { return keyId; }
    public String getAlgorithm() { return algorithm; }
    public byte[] getKeyData() { return keyData; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getExpiresAt() { return expiresAt; }
    
    // optinal set expiration
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}
