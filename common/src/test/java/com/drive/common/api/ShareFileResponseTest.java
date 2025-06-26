package com.drive.common.api;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ShareFileResponseTest {
    @Test
    public void testImmutableConstructionAndGetters() {
        UUID fileId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();
        UUID recipientId = UUID.randomUUID();
        ShareFileResponse.PermissionType permission = ShareFileResponse.PermissionType.ADMIN;
        String encryptedAccessKey = "enc-key-123";
        Instant grantedAt = Instant.now();
        Instant expiresAt = Instant.now();

        ShareFileResponse resp = new ShareFileResponse(fileId, permissionId, recipientId, permission, encryptedAccessKey, grantedAt, expiresAt);

        assertEquals(fileId, resp.getFileId());
        assertEquals(permissionId, resp.getPermissionId());
        assertEquals(recipientId, resp.getRecipientId());
        assertEquals(permission, resp.getPermission());
        assertEquals(encryptedAccessKey, resp.getEncryptedAccessKey());
        assertEquals(grantedAt, resp.getGrantedAt());
        assertEquals(expiresAt, resp.getExpiresAt());
    }
} 