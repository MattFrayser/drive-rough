package com.drive.storage.repository;

import com.drive.storage.model.FileMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFileRepositoryTest {

    private InMemoryFileRepository repository;
    private FileMetadata testFile;

    @BeforeEach
    void setUp() {
        repository = new InMemoryFileRepository();
        
        testFile = new FileMetadata();
        testFile.setId(UUID.randomUUID());
        testFile.setFilename("test.txt");
        testFile.setOwnerId(UUID.randomUUID());
        testFile.setContentType("text/plain");
        testFile.setSize(100L);
        testFile.setStoragePath("/storage/test.txt");
        testFile.setChecksum("abc123");
        testFile.setCreatedAt(Instant.now());
        testFile.setUpdatedAt(Instant.now());
    }

    @Test
    void testSave() {
        FileMetadata savedFile = repository.save(testFile);
        
        assertNotNull(savedFile);
        assertEquals(testFile.getId(), savedFile.getId());
        assertEquals(testFile.getFilename(), savedFile.getFilename());
        assertEquals(testFile.getOwnerId(), savedFile.getOwnerId());
    }

    @Test
    void testFindById() {
        repository.save(testFile);
        
        Optional<FileMetadata> found = repository.findById(testFile.getId());
        
        assertTrue(found.isPresent());
        assertEquals(testFile.getId(), found.get().getId());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<FileMetadata> found = repository.findById(UUID.randomUUID());
        
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByOwnerId() {
        UUID ownerId = UUID.randomUUID();
        testFile.setOwnerId(ownerId);
        repository.save(testFile);
        
        // Create another file with different owner
        FileMetadata anotherFile = new FileMetadata();
        anotherFile.setId(UUID.randomUUID());
        anotherFile.setFilename("another.txt");
        anotherFile.setOwnerId(UUID.randomUUID());
        anotherFile.setContentType("text/plain");
        anotherFile.setSize(50L);
        anotherFile.setStoragePath("/storage/another.txt");
        anotherFile.setChecksum("def456");
        anotherFile.setCreatedAt(Instant.now());
        anotherFile.setUpdatedAt(Instant.now());
        repository.save(anotherFile);
        
        List<FileMetadata> ownerFiles = repository.findByOwnerId(ownerId);
        
        assertEquals(1, ownerFiles.size());
        assertEquals(testFile.getId(), ownerFiles.get(0).getId());
    }

    @Test
    void testDeleteById() {
        repository.save(testFile);
        
        assertTrue(repository.findById(testFile.getId()).isPresent());
        
        repository.deleteById(testFile.getId());
        
        assertFalse(repository.findById(testFile.getId()).isPresent());
    }

    @Test
    void testDeleteByIdNotFound() {
        // Should not throw exception
        assertDoesNotThrow(() -> repository.deleteById(UUID.randomUUID()));
    }

    @Test
    void testExistsById() {
        assertFalse(repository.existsById(testFile.getId()));
        
        repository.save(testFile);
        
        assertTrue(repository.existsById(testFile.getId()));
    }

    @Test
    void testFindAll() {
        assertTrue(repository.findAll().isEmpty());
        
        repository.save(testFile);
        
        FileMetadata anotherFile = new FileMetadata();
        anotherFile.setId(UUID.randomUUID());
        anotherFile.setFilename("another.txt");
        anotherFile.setOwnerId(UUID.randomUUID());
        anotherFile.setContentType("text/plain");
        anotherFile.setSize(50L);
        anotherFile.setStoragePath("/storage/another.txt");
        anotherFile.setChecksum("def456");
        anotherFile.setCreatedAt(Instant.now());
        anotherFile.setUpdatedAt(Instant.now());
        repository.save(anotherFile);
        
        List<FileMetadata> allFiles = repository.findAll();
        assertEquals(2, allFiles.size());
    }
}
