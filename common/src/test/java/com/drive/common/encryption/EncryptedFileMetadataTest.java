package com.drive.common.encryption;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class EncryptedFileMetadataTest {
    @Test
    public void testConstructionAndGettersSetters() {
        EncryptedFileMetadata meta = new EncryptedFileMetadata();
        UUID id = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        String storagePath = "/files/abc";
        long encryptedSize = 12345L;
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();
        String algorithm = "AES-GCM";
        String keyId = "key-123";
        String encryptedChecksum = "checksum";
        String encryptedFilename = "file.enc";
        String encryptedContentType = "application/octet-stream";
        String accessKeyEncrypted = "access-key";

        meta.setId(id);
        meta.setOwnerId(ownerId);
        meta.setStoragePath(storagePath);
        meta.setEncryptedSize(encryptedSize);
        meta.setCreatedAt(createdAt);
        meta.setUpdatedAt(updatedAt);
        meta.setAlgorithm(algorithm);
        meta.setKeyId(keyId);
        meta.setEncryptedChecksum(encryptedChecksum);
        meta.setEncryptedFilename(encryptedFilename);
        meta.setEncryptedContentType(encryptedContentType);
        meta.setAcccessKeyEncrypted(accessKeyEncrypted);

        assertEquals(id, meta.getId());
        assertEquals(ownerId, meta.getOwnerId());
        assertEquals(storagePath, meta.getStoragePath());
        assertEquals(encryptedSize, meta.getEncryptedSize());
        assertEquals(createdAt, meta.getCreatedAt());
        assertEquals(updatedAt, meta.getUpdatedAt());
        assertEquals(algorithm, meta.getAlgorithm());
        assertEquals(keyId, meta.getKeyId());
        assertEquals(encryptedChecksum, meta.getEncryptedChecksum());
        assertEquals(encryptedFilename, meta.getEncryptedFilename());
        assertEquals(encryptedContentType, meta.getEncryptedContentType());
        assertEquals(accessKeyEncrypted, meta.getAcccessKeyEncrypted());
    }
} 