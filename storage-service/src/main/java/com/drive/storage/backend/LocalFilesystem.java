//========================================================================
// Local Filesystem
// ========================================================================

package com.drive.storage.backend;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

public class LocalFilesystem implements StorageBackend {

    @Value("${storage.local.basePath:./storage}")
    protected String basePath;

    @Override
    public String store(InputStream inputStream, String filename) throws Exception {

        // Create storage dir if it doesn't exist
        Path storageDir = Paths.get(basePath);
        Files.createDirectories(storageDir);

        // Generate unique filename
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;
        Path filePath = storageDir.resolve(uniqueFilename);

        // Copy input stream to filename 
        try (OutputStream outputStream = Files.newOutputStream(filePath)) {
            inputStream.transferTo(outputStream);
        }
        
        return uniqueFilename;
    }

    @Override
    public InputStream retrieve(String storagePath) throws Exception {
        Path filePath = Paths.get(basePath, storagePath);
        if (!Files.exists(filePath)) {
            throw new Exception("File not found: " + filePath);
        }
        return Files.newInputStream(filePath);
    }

    @Override
    public void delete(String storagePath) throws Exception {
           Path filePath = Paths.get(basePath, storagePath);
           Files.deleteIfExists(filePath);  
    }

    @Override
    public boolean exists(String storagePath) {
        Path filePath = Paths.get(basePath, storagePath);
        return Files.exists(filePath);
    } 
}
