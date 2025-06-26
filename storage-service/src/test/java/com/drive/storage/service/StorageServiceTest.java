package com.drive.storage.service;

import com.drive.storage.backend.StorageBackend;
import com.drive.storage.model.FileInfo;
import com.drive.storage.model.FileMetadata;
import com.drive.storage.model.UploadResponse;
import com.drive.storage.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @Mock
    private StorageBackend storageBackend;

    @Mock
    private FileRepository fileRepository;

    private StorageService storageService;

    private UUID testUserId;
    private MockMultipartFile testFile;
    private FileMetadata testMetadata;

    @BeforeEach
    void setUp() {
        storageService = new StorageService(storageBackend, fileRepository);
        testUserId = UUID.randomUUID();
        
        testFile = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            "Hello, World!".getBytes()
        );

        testMetadata = new FileMetadata();
        testMetadata.setId(UUID.randomUUID());
        testMetadata.setFilename("test.txt");
        testMetadata.setContentType("text/plain");
        testMetadata.setSize(13L);
        testMetadata.setOwnerId(testUserId);
        testMetadata.setStoragePath("/storage/test.txt");
        testMetadata.setChecksum("abc123");
        testMetadata.setCreatedAt(Instant.now());
        testMetadata.setUpdatedAt(Instant.now());
    }

    @Test
    void testUploadFile() throws Exception {
        String storagePath = "/storage/unique-file.txt";
        when(storageBackend.store(any(InputStream.class), eq("test.txt"))).thenReturn(storagePath);
        when(fileRepository.save(any(FileMetadata.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UploadResponse response = storageService.uploadFile(testFile, testUserId);

        assertNotNull(response);
        assertNotNull(response.getFileId());
        assertEquals("test.txt", response.getFileName());
        assertEquals(13L, response.getSize());
        assertEquals("text/plain", response.getContentType());
        assertNotNull(response.getUploadedAt());

        verify(storageBackend).store(any(InputStream.class), eq("test.txt"));
        verify(fileRepository).save(argThat((FileMetadata metadata) -> 
            metadata.getFilename().equals("test.txt") &&
            metadata.getOwnerId().equals(testUserId) &&
            metadata.getStoragePath().equals(storagePath)
        ));
    }

    @Test
    void testUploadEmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile(
            "file", "empty.txt", "text/plain", new byte[0]
        );

        assertThrows(IllegalArgumentException.class, () -> 
            storageService.uploadFile(emptyFile, testUserId)
        );

        try {
            verify(storageBackend, never()).store(any(), any());
        } catch (Exception e) {
            fail("Mock verify failed: " + e.getMessage());
        }
        verify(fileRepository, never()).save(any());
    }

    @Test
    void testDownloadFile() {
        InputStream fileContent = new ByteArrayInputStream("Hello, World!".getBytes());
        
        when(fileRepository.findById(testMetadata.getId())).thenReturn(Optional.of(testMetadata));
        try {
            when(storageBackend.retrieve(testMetadata.getStoragePath())).thenReturn(fileContent);
        } catch (Exception e) {
            fail("Mock setup failed: " + e.getMessage());
        }

        try {
            InputStream result = storageService.downloadFile(testMetadata.getId(), testUserId);
            assertNotNull(result);
            assertEquals("Hello, World!", new String(result.readAllBytes()));
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }

        verify(fileRepository).findById(testMetadata.getId());
        try {
            verify(storageBackend).retrieve(testMetadata.getStoragePath());
        } catch (Exception e) {
            fail("Mock verify failed: " + e.getMessage());
        }
    }

    @Test
    void testDownloadFileNotFound() {
        UUID nonExistentFileId = UUID.randomUUID();
        when(fileRepository.findById(nonExistentFileId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> 
            storageService.downloadFile(nonExistentFileId, testUserId)
        );

        try {
            verify(storageBackend, never()).retrieve(any());
        } catch (Exception e) {
            fail("Mock verify failed: " + e.getMessage());
        }
    }

    @Test
    void testDownloadFileAccessDenied() {
        UUID otherUserId = UUID.randomUUID();
        when(fileRepository.findById(testMetadata.getId())).thenReturn(Optional.of(testMetadata));

        assertThrows(SecurityException.class, () -> 
            storageService.downloadFile(testMetadata.getId(), otherUserId)
        );

        try {
            verify(storageBackend, never()).retrieve(any());
        } catch (Exception e) {
            fail("Mock verify failed: " + e.getMessage());
        }
    }

    @Test
    void testListUserFiles() {
        FileMetadata file1 = createFileMetadata("file1.txt", testUserId);
        FileMetadata file2 = createFileMetadata("file2.txt", testUserId);
        
        when(fileRepository.findByOwnerId(testUserId)).thenReturn(Arrays.asList(file1, file2));

        try {
            List<FileInfo> result = storageService.listUserFiles(testUserId);
            assertEquals(2, result.size());
            assertTrue(result.stream().anyMatch(f -> f.getFilename().equals("file1.txt")));
            assertTrue(result.stream().anyMatch(f -> f.getFilename().equals("file2.txt")));
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }

        verify(fileRepository).findByOwnerId(testUserId);
    }

    @Test
    void testDeleteFile() {
        when(fileRepository.findById(testMetadata.getId())).thenReturn(Optional.of(testMetadata));

        try {
            storageService.deleteFile(testMetadata.getId(), testUserId);
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }

        verify(fileRepository).findById(testMetadata.getId());
        try {
            verify(storageBackend).delete(testMetadata.getStoragePath());
        } catch (Exception e) {
            fail("Mock verify failed: " + e.getMessage());
        }
        verify(fileRepository).deleteById(testMetadata.getId());
    }

    @Test
    void testDeleteFileNotFound() {
        UUID nonExistentFileId = UUID.randomUUID();
        when(fileRepository.findById(nonExistentFileId)).thenReturn(Optional.empty());

        try {
            assertThrows(IllegalArgumentException.class, () ->
                storageService.deleteFile(nonExistentFileId, testUserId)
            );
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }

        try {
            verify(storageBackend, never()).delete(any());
        } catch (Exception e) {
            fail("Mock verify failed: " + e.getMessage());
        }
        verify(fileRepository, never()).deleteById(any());
    }

    @Test
    void testDeleteFileAccessDenied() {
        UUID otherUserId = UUID.randomUUID();
        when(fileRepository.findById(testMetadata.getId())).thenReturn(Optional.of(testMetadata));

        try {
            assertThrows(SecurityException.class, () ->
                storageService.deleteFile(testMetadata.getId(), otherUserId)
            );
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }

        try {
            verify(storageBackend, never()).delete(any());
        } catch (Exception e) {
            fail("Mock verify failed: " + e.getMessage());
        }
        verify(fileRepository, never()).deleteById(any());
    }

    @Test
    void testGetFileMetadata() {
        when(fileRepository.findById(testMetadata.getId())).thenReturn(Optional.of(testMetadata));

        try {
            FileMetadata result = storageService.getFileMetadata(testMetadata.getId(), testUserId);
            assertNotNull(result);
            assertEquals(testMetadata.getFilename(), result.getFilename());
            assertEquals(testMetadata.getOwnerId(), result.getOwnerId());
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }

        verify(fileRepository).findById(testMetadata.getId());
    }

    @Test
    void testGetFileMetadataNotFound() {
        UUID nonExistentFileId = UUID.randomUUID();
        when(fileRepository.findById(nonExistentFileId)).thenReturn(Optional.empty());

        try {
            assertThrows(IllegalArgumentException.class, () ->
                storageService.getFileMetadata(nonExistentFileId, testUserId)
            );
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void testGetFileMetadataAccessDenied() {
        UUID otherUserId = UUID.randomUUID();
        when(fileRepository.findById(testMetadata.getId())).thenReturn(Optional.of(testMetadata));

        try {
            assertThrows(SecurityException.class, () ->
                storageService.getFileMetadata(testMetadata.getId(), otherUserId)
            );
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void testChecksumCalculation() {
        String storagePath = "/storage/checksum-test.txt";
        try {
            when(storageBackend.store(any(InputStream.class), eq("test.txt"))).thenReturn(storagePath);
        } catch (Exception e) {
            fail("Mock setup failed: " + e.getMessage());
        }
        when(fileRepository.save(any(FileMetadata.class))).thenAnswer(invocation -> invocation.getArgument(0));

        try {
            UploadResponse response = storageService.uploadFile(testFile, testUserId);
            verify(fileRepository).save(argThat((FileMetadata metadata) -> 
                metadata.getChecksum() != null && !metadata.getChecksum().isEmpty()
            ));
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    private FileMetadata createFileMetadata(String filename, UUID ownerId) {
        FileMetadata metadata = new FileMetadata();
        metadata.setId(UUID.randomUUID());
        metadata.setFilename(filename);
        metadata.setContentType("text/plain");
        metadata.setSize(100L);
        metadata.setOwnerId(ownerId);
        metadata.setStoragePath("/storage/" + filename);
        metadata.setChecksum("checksum");
        metadata.setCreatedAt(Instant.now());
        metadata.setUpdatedAt(Instant.now());
        return metadata;
    }
}
