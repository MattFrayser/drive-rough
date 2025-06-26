package com.drive.common.api;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class EncryptedUploadResponseTest {
    @Test
    public void testImmutableConstructionAndGetters() {
        UUID fileId = UUID.randomUUID();
        String encryptedFileName = "file.enc";
        long encryptedSize = 2048L;
        long originalSize = 4096L;
        String algorithm = "AES-GCM";
        String keyId = "key-abc";
        Instant uploadedAt = Instant.now();

        EncryptedUploadResponse resp = new EncryptedUploadResponse(fileId, encryptedFileName, encryptedSize, originalSize, algorithm, keyId, uploadedAt);

        assertEquals(fileId, resp.getFileId());
        assertEquals(encryptedFileName, resp.getEncryptedFileName());
        assertEquals(encryptedSize, resp.getEncryptedSize());
        assertEquals(originalSize, resp.getOriginalSize());
        assertEquals(algorithm, resp.getAlgorithm());
        assertEquals(keyId, resp.getKeyId());
        assertEquals(uploadedAt, resp.getUploadedAt());
    }
} 