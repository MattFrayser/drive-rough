// ============================================================================
// Temp in memory repo for testing
// ============================================================================

package com.drive.storage.repository;

import com.drive.storage.model.FileMetadata;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFileRepository implements FileRepository {

    private Map<UUID, FileMetadata> files = new HashMap<>();

    @Override
    public FileMetadata save(FileMetadata metadata) {
        files.put(metadata.getId(), metadata);
        return metadata;
    }

    @Override
    public Optional<FileMetadata> findById(UUID id) {
        return Optional.ofNullable(files.get(id));
    }

    @Override
    public List<FileMetadata> findByOwnerId(UUID ownerId) {
        return files.values().stream()
            .filter(file -> file.getOwnerId().equals(ownerId))
            .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        files.remove(id);
    }

    @Override
    public List<FileMetadata> findAll() {
        return new ArrayList<>(files.values());
    }
    
    @Override
    public boolean existsById(UUID id) {
        return files.containsKey(id);
    }
}       
