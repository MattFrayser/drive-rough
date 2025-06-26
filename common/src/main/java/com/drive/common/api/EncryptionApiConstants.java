package com.drive.common.api;

public class EncryptionApiConstants {

    // Encryption algorithms
    public static final String AES_GCM_256 = "AES/GCM/NoPadding";
    public static final String RSA_OEAP = "RSA/ECB/OAEPPadding";

    // Permission levels
    public static final String READ = "READ";
    public static final String WRITE = "WRITE";
    public static final String ADMIN = "ADMIN";
    
    // Key operations
    public static final String ROTATE = "ROTATE";
    public static final String REVOKE = "REVOKE";
    public static final String GRANT_ACCESS = "GRANT_ACCESS";

    // Error codes
    public static final String ERROR_ENCRYPTION_FAILED = "ENCRYPTION_FAILED";
    public static final String ERROR_DECRYPTION_FAILED = "DECRYPTION_FAILED";
    public static final String ERROR_KEY_NOT_FOUND = "KEY_NOT_FOUND";
    public static final String ERROR_ACCESS_DENIED = "ACCESS_DENIED";
    public static final String ERROR_INVALID_ALGORITHM = "INVALID_ALGORITHM";
    public static final String ERROR_INVALID_KEY = "INVALID_KEY";
    public static final String ERROR_INVALID_FILE = "INVALID_FILE";
    public static final String ERROR_INVALID_PERMISSION = "INVALID_PERMISSION";
    public static final String ERROR_INVALID_REQUEST = "INVALID_REQUEST";
    public static final String ERROR_UNAUTHORIZED = "UNAUTHORIZED";
    public static final String ERROR_INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    public static final String ERROR_FILE_NOT_FOUND = "FILE_NOT_FOUND";

    private EncryptionApiConstants() {}
}
