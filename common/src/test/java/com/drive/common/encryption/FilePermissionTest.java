package com.drive.common.encryption;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class FilePermissionTest {
    @Test
    public void testConstructionAndGettersSetters() {
        FilePermission perm = new FilePermission();
        UUID id = UUID.randomUUID();
        UUID fileId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        FilePermission.PermissionType permission = FilePermission.PermissionType.READ;
        String encryptedAccessKey = "enc-key";
        Instant grantedAt = Instant.now();
        Instant expiresAt = Instant.now().plusSeconds(3600);

        perm.setId(id);
        perm.setFileId(fileId);
        perm.setUserId(userId);
        perm.setPermission(permission);
        perm.setEncryptedAccessKey(encryptedAccessKey);
        perm.setGrantedAt(grantedAt);
        perm.setExpiresAt(expiresAt);

        assertEquals(id, perm.getId());
        assertEquals(fileId, perm.getFileId());
        assertEquals(userId, perm.getUserId());
        assertEquals(permission, perm.getPermission());
        assertEquals(encryptedAccessKey, perm.getEncryptedAccessKey());
        assertEquals(grantedAt, perm.getGrantedAt());
        assertEquals(expiresAt, perm.getExpiresAt());
    }
} 