package com.drive.common.encryption;

public class EncryptedFileContent {
    private String algorithm;
    private String iv; // Initialization vector
    private byte[] encryptedData;
    private String keyId;
    
    public EncryptedFileContent(String algorithm, String iv, byte[] encryptedData, String keyId) {
        this.algorithm = algorithm;
        this.iv = iv;
        this.encryptedData = encryptedData;
        this.keyId = keyId;
    }
    
    public String getAlgorithm() { return algorithm; }
    public String getIv() { return iv; }
    public byte[] getEncryptedData() { return encryptedData; }
    public String getKeyId() { return keyId; }
}
