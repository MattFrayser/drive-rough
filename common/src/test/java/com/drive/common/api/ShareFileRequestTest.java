package com.drive.common.api;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ShareFileRequestTest {
    @Test
    public void testImmutableConstructionAndGetters() {
        UUID fileId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ShareFileRequest.PermissionType permission = ShareFileRequest.PermissionType.WRITE;
        Instant expiresAt = Instant.now();

        ShareFileRequest req = new ShareFileRequest(fileId, userId, permission, expiresAt);

        assertEquals(fileId, req.getFileId());
        assertEquals(userId, req.getUserId());
        assertEquals(permission, req.getPermission());
        assertEquals(expiresAt, req.getExpiresAt());
    }
} 