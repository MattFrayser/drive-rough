// ============================================================================
// Implementation of EncryptionService
// =============================================================================

package com.drive.common.encryption.impl;

import com.drive.common.encryption.*;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.UUID;
import java.util.Base64;

@Service
public class EncryptionServiceImpl implements EncryptionService {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int KEY_LENGTH = 256;

    private static final SecureRandom secureRandom = new SecureRandom();
    
    /**
     * Encrypt File
     */
    @Override
    public EncryptedFileContent encryptFile(byte[] plainData, UUID userId) throws Exception {

        // Generate file-specific key 
        EncryptionKey fileKey = generateFileKey();

        // Genrate IV 
        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);

        // Encrypt file data 
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        SecretKey secretKey = new SecretKeySpec(fileKey.getKeyData(), ALGORITHM);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);
        byte[] encryptedData = cipher.doFinal(plainData);

        return new EncryptedFileContent(
            TRANSFORMATION,
            Base64.getEncoder().encodeToString(iv),
            encryptedData,
            fileKey.getKeyId()
        );
    }

    @Override
    public byte[] decryptFile(EncryptedFileContent encryptedContent, UUID userId) throws Exception {
        // TODO: Retrive file bases on keyId and userId

        throw new UnsupportedOperationException("Key managemetn Not implemented yet");
    }

    @Override
    public String encryptMetadata(String plaintext, UUID userId) throws Exception {
        // TODO: Implement user-specific metadata encryption key derivation
        throw new UnsupportedOperationException("Key managememt Not implemented yet");
    }

    @Override 
    public String decryptMetadata(String encryptedMetadata, UUID userId) throws Exception {

        SecretKey metadataKey = deriveMetadataKey(userId);

        byte[] combined = Base64.getDecoder().decode(encryptedMetadata);

        // Extract IV and encrypted data
        byte[] iv = new byte[GCM_IV_LENGTH];
        byte[] encrypted = new byte[combined.length - GCM_IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);
        System.arraycopy(combined, GCM_IV_LENGTH, encrypted, 0, encrypted.length);

        // Decrypt data 
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.DECRYPT_MODE, metadataKey, gcmSpec);

        byte[] deccryptedData = cipher.doFinal(encrypted);
        return new String(deccryptedData);
    }

    @Override 
    public EncryptionKey generateFileKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(KEY_LENGTH);
        SecretKey secretKey = keyGen.generateKey();

        String keyId = UUID.randomUUID().toString();
        return new EncryptionKey(keyId, ALGORITHM, secretKey.getEncoded());
    }

    @Override
    public String encryptAccessKey(EncryptionKey fileKey, String recipientPublicKey) throws Exception {
        // TODO: Implement RSA encryption of file key and access keyGen
        throw new UnsupportedOperationException("Key managememt Not implemented yet");
    }

    @Override
    public String decryptAccessKey(String encryptedAccessKey, String userPrivateKey) throws Exception {
        // TODO: Implement RSA decryption and private key decryption
        throw new UnsupportedOperationException("Key managememt Not implemented yet");
    }

    private SecretKey deriveMetadataKey(UUID userId) throws Exception {
        // TODO: Implement proper key derivation from users private keyGen
        throw new UnsupportedOperationException("Key managememt Not implemented yet");
    }
}
