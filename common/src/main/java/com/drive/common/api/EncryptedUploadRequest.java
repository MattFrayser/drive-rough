// ============================================================================
// Encypted Request for file upload
// =============================================================================

package com.drive.common.api;

/**
 * Immutable DTO for encrypted file upload requests.
 */
public class EncryptedUploadRequest {

    private final String encryptedFileName;
    private final String encryptedContentType;
    private final String algorithm;
    private final String keyId;
    private final long originalSize;

    public EncryptedUploadRequest(String encryptedFileName, String encryptedContentType, String algorithm, String keyId, long originalSize) {
        this.encryptedFileName = encryptedFileName;
        this.encryptedContentType = encryptedContentType;
        this.algorithm = algorithm;
        this.keyId = keyId;
        this.originalSize = originalSize;
    }

    public String getEncryptedFileName() { return encryptedFileName; }
    public String getEncryptedContentType() { return encryptedContentType; }
    public String getAlgorithm() { return algorithm; }
    public String getKeyId() { return keyId; }
    public long getOriginalSize() { return originalSize; }
}


