package com.drive.common.encryption;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EncryptedFileContentTest {
    @Test
    public void testConstructionAndGetters() {
        String algorithm = "AES-GCM";
        String iv = "iv-123";
        byte[] encryptedData = new byte[] {1, 2, 3, 4, 5};
        String keyId = "key-abc";

        EncryptedFileContent content = new EncryptedFileContent(algorithm, iv, encryptedData, keyId);

        assertEquals(algorithm, content.getAlgorithm());
        assertEquals(iv, content.getIv());
        assertArrayEquals(encryptedData, content.getEncryptedData());
        assertEquals(keyId, content.getKeyId());
    }
} 