// ============================================================================
// Repository interface for file metadata operations
// ============================================================================

package com.drive.storage.repository;

import com.drive.storage.model.FileMetadata;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileRepository {

    FileMetadata save(FileMetadata metadata);
    
    Optional<FileMetadata> findById(UUID id);
    
    List<FileMetadata> findByOwnerId(UUID ownerId);
    
    void deleteById(UUID id);
    
    List<FileMetadata> findAll();
    
    boolean existsById(UUID id);
} 