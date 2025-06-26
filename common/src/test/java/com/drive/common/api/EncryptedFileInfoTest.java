package com.drive.common.api;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class EncryptedFileInfoTest {
    @Test
    public void testImmutableConstructionAndGetters() {
        UUID id = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        String encryptedFileName = "file.enc";
        long encryptedSize = 1024L;
        long originalSize = 2048L;
        String algorithm = "AES-GCM";
        String keyId = "key-xyz";
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();

        EncryptedFileInfo info = new EncryptedFileInfo(id, ownerId, encryptedFileName, encryptedSize, originalSize, algorithm, keyId, createdAt, updatedAt);

        assertEquals(id, info.getId());
        assertEquals(ownerId, info.getOwnerId());
        assertEquals(encryptedFileName, info.getEncryptedFileName());
        assertEquals(encryptedSize, info.getEncryptedSize());
        assertEquals(originalSize, info.getOriginalSize());
        assertEquals(algorithm, info.getAlgorithm());
        assertEquals(keyId, info.getKeyId());
        assertEquals(createdAt, info.getCreatedAt());
        assertEquals(updatedAt, info.getUpdatedAt());
    }
} 