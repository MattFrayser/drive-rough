package com.drive.common.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EncryptedUploadRequestTest {
    @Test
    public void testImmutableConstructionAndGetters() {
        String fileName = "abc123.enc";
        String contentType = "application/octet-stream";
        String algorithm = "AES-GCM";
        String keyId = "key-xyz";
        long originalSize = 123456L;

        EncryptedUploadRequest req = new EncryptedUploadRequest(fileName, contentType, algorithm, keyId, originalSize);

        Assertions.assertEquals(fileName, req.getEncryptedFileName());
        Assertions.assertEquals(contentType, req.getEncryptedContentType());
        Assertions.assertEquals(algorithm, req.getAlgorithm());
        Assertions.assertEquals(keyId, req.getKeyId());
        Assertions.assertEquals(originalSize, req.getOriginalSize());
    }
} 