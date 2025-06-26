package com.drive.storage.util;

import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class TestFileFactory {
    
    public static MockMultipartFile createTextFile(String filename, String content) {
        return new MockMultipartFile(
            "file",
            filename,
            "text/plain",
            content.getBytes()
        );
    }
    
    public static MockMultipartFile createEmptyFile() {
        return new MockMultipartFile(
            "file",
            "empty.txt",
            "text/plain",
            new byte[0]
        );
    }
    
    public static MockMultipartFile createBinaryFile(String filename, byte[] content) {
        return new MockMultipartFile(
            "file",
            filename,
            "application/octet-stream",
            content
        );
    }
    
    public static MockMultipartFile createImageFile(String filename, int sizeKb) {
        // Create dummy image data
        byte[] imageData = new byte[sizeKb * 1024];
        // Fill with some pattern to simulate image data
        for (int i = 0; i < imageData.length; i++) {
            imageData[i] = (byte) (i % 256);
        }
        
        return new MockMultipartFile(
            "file",
            filename,
            "image/jpeg",
            imageData
        );
    }
    
    public static MockMultipartFile createLargeFile(String filename, int sizeMb) {
        byte[] largeData = new byte[sizeMb * 1024 * 1024];
        // Fill with some pattern
        for (int i = 0; i < largeData.length; i++) {
            largeData[i] = (byte) (i % 256);
        }
        
        return new MockMultipartFile(
            "file",
            filename,
            "application/octet-stream",
            largeData
        );
    }
    
    public static InputStream createTextInputStream(String content) {
        return new ByteArrayInputStream(content.getBytes());
    }
}
