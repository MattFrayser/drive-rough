package com.drive.common.api;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EncryptionErrorResponseTest {
    @Test
    public void testImmutableConstructionAndGetters() {
        String error = "Encryption failed";
        String code = "ENCRYPTION_FAILED";
        String message = "Unable to encrypt file.";

        EncryptionErrorResponse resp = new EncryptionErrorResponse(error, code, message);

        assertEquals(error, resp.getError());
        assertEquals(code, resp.getCode());
        assertEquals(message, resp.getMessage());
        assertNotNull(resp.getTimestamp());
    }
} 