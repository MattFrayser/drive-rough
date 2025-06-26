// ============================================================================
// Core encryption operations
// Define contract for encryption
// =============================================================================

package com.drive.common.encryption;

import java.io.InputStream;
import java.util.UUID;

public interface EncryptionService {

    /**
     * Encrypt File (AES-256-GCM)
     * @param plainData - Raw file data 
     * @param userId - Owners ID for key derivation
     * @return EncryptedFile - Encrypted file content w/ metadata
     */
    EncryptedFileContent encryptFile(byte[] plainData, UUID userId) throws Exception;

    /**
     * Decrypt File
     * @param encryptedContent - Encrypted file content 
     * @param userId - User requesting decryption
     * @return Decrypted file data
     */
    byte[] decryptFile(EncryptedFileContent encryptedContent, UUID userId) throws Exception;

    /**
     * Encrypt File Metadata
     * @param plaintext - File metadata to encrypt
     * @param userId - user ID for key derivation
     * @return EncryptedMetadata - Encrypted file metadata
     */
    String encryptMetadata(String plaintext, UUID userId) throws Exception;

    /**
     * Decrpt File Metadata
     * @param encryptedMetadata - Encrypted file metadata
     * @param userId - user ID for key derivation
     * @return Decrypted plaintext
     */
    String decryptMetadata(String encryptedMetadata, UUID userId) throws Exception;

    /**
     * Generate file-specific encryption key
     * @return new encryption key
     */
    EncryptionKey generateFileKey() throws Exception;

    /**
     * Encrypt access key for sharing
     * @param fileKey Original file encryption key
     * @param recipientPublicKey Public key of user being granted access
     * @return Encrypted access key
     */
    String encryptAccessKey(EncryptionKey fileKey, String recipientPublicKey) throws Exception;
    
    /**
     * Decrypt access key for file access
     * @param encryptedAccessKey - Encrypted access key 
     * @param userPrivateKey - Users private key
     * @return Decrypted access key
     */
    String decryptAccessKey(String encryptedAccessKey, String userPrivateKey) throws Exception;
}
