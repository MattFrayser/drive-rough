// ============================================================================
// Interface - Switch between different storage types
// ============================================================================

package com.drive.storage.backend;

import java.io.InputStream;

public interface Storagebackend {

    // Store file
    String store(InputStream inputStream, String filename) throws Exception;

    // Retrieve file via path
    InputStream retrieve(String path) throws Exception;

    // Delete file via path
    void delete(String path) throws Exception;
    
    // Check if file exists
    boolean exists(String storagePath);
}   
    
