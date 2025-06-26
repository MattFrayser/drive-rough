package com.drive.common.encryption;

import org.junit.jupiter.api.Test;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class EncryptionKeyTest {
    @Test
    public void testConstructionAndGetters() {
        String keyId = "key-xyz";
        String algorithm = "AES-GCM";
        byte[] keyData = new byte[] {10, 20, 30};

        EncryptionKey key = new EncryptionKey(keyId, algorithm, keyData);

        assertEquals(keyId, key.getKeyId());
        assertEquals(algorithm, key.getAlgorithm());
        assertArrayEquals(keyData, key.getKeyData());
        assertNotNull(key.getCreatedAt());
        assertNull(key.getExpiresAt());
    }

    @Test
    public void testSetExpiresAt() {
        EncryptionKey key = new EncryptionKey("id", "alg", new byte[] {1});
        Instant expires = Instant.now().plusSeconds(3600);
        key.setExpiresAt(expires);
        assertEquals(expires, key.getExpiresAt());
    }
} 