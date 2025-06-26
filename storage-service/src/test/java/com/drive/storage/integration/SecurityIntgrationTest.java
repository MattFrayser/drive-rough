// ============================================================================
// Integration Tests for Storage Service
// ============================================================================

package com.drive.storage.integration;

import com.drive.storage.model.FileInfo;
import com.drive.storage.model.FileMetadata;
import com.drive.storage.model.UploadResponse;
import com.drive.storage.repository.InMemoryFileRepository;
import com.drive.storage.service.StorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(properties = {
    "storage.local.basePath=${java.io.tmpdir}/storage-test",
    "logging.level.com.drive.storage=DEBUG"
})
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StorageService storageService;

    @Autowired
    private InMemoryFileRepository fileRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @TempDir
    Path tempDir;

    private UUID testUserId;
    private UUID anotherUserId;
    private String validJwtToken;

    @BeforeEach
    void setUp() {
        // Clear repository before each test
        fileRepository.findAll().forEach(file -> fileRepository.deleteById(file.getId()));
        
        testUserId = UUID.randomUUID();
        anotherUserId = UUID.randomUUID();
        
        // For now, we'll create a mock token - in real tests you'd generate valid JWTs
        validJwtToken = "Bearer valid-jwt-token";
    }

    @Test
    void testUploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            "Hello, World!".getBytes()
        );

        UploadResponse response = storageService.uploadFile(file, testUserId);

        assertNotNull(response);
        assertNotNull(response.getFileId());
        assertEquals("test.txt", response.getFileName());
        assertEquals(13, response.getSize()); // "Hello, World!" length
        assertEquals("text/plain", response.getContentType());
        assertNotNull(response.getUploadedAt());
    }

    @Test
    void testUploadEmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile(
            "file",
            "empty.txt",
            "text/plain",
            new byte[0]
        );

        assertThrows(IllegalArgumentException.class, () -> {
            storageService.uploadFile(emptyFile, testUserId);
        });
    }

    @Test
    void testDownloadFile() throws Exception {
        // Upload file first
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            "Hello, World!".getBytes()
        );

        UploadResponse uploadResponse = storageService.uploadFile(file, testUserId);
        
        // Download the file
        InputStream downloadedFile = storageService.downloadFile(uploadResponse.getFileId(), testUserId);
        
        assertNotNull(downloadedFile);
        
        // Verify content
        byte[] downloadedBytes = downloadedFile.readAllBytes();
        assertEquals("Hello, World!", new String(downloadedBytes));
    }

    @Test
    void testDownloadFileAccessControl() throws Exception {
        // Upload file as testUserId
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            "Hello, World!".getBytes()
        );

        UploadResponse uploadResponse = storageService.uploadFile(file, testUserId);
        
        // Try to download as different user
        assertThrows(SecurityException.class, () -> {
            storageService.downloadFile(uploadResponse.getFileId(), anotherUserId);
        });
    }

    @Test
    void testListUserFiles() throws Exception {
        // Upload multiple files
        MockMultipartFile file1 = new MockMultipartFile(
            "file", "test1.txt", "text/plain", "Content 1".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile(
            "file", "test2.txt", "text/plain", "Content 2".getBytes()
        );

        storageService.uploadFile(file1, testUserId);
        storageService.uploadFile(file2, testUserId);
        
        // Upload file for different user
        storageService.uploadFile(file1, anotherUserId);

        // List files for testUserId
        List<FileInfo> userFiles = storageService.listUserFiles(testUserId);
        
        assertEquals(2, userFiles.size());
        assertTrue(userFiles.stream().anyMatch(f -> f.getFilename().equals("test1.txt")));
        assertTrue(userFiles.stream().anyMatch(f -> f.getFilename().equals("test2.txt")));
    }

    @Test
    void testDeleteFile() throws Exception {
        // Upload file
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.txt", "text/plain", "Hello, World!".getBytes()
        );

        UploadResponse uploadResponse = storageService.uploadFile(file, testUserId);
        UUID fileId = uploadResponse.getFileId();
        
        // Verify file exists
        FileMetadata metadata = storageService.getFileMetadata(fileId, testUserId);
        assertNotNull(metadata);
        
        // Delete file
        storageService.deleteFile(fileId, testUserId);
        
        // Verify file is deleted
        assertThrows(IllegalArgumentException.class, () -> {
            storageService.getFileMetadata(fileId, testUserId);
        });
    }

    @Test
    void testDeleteFileAccessControl() throws Exception {
        // Upload file as testUserId
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.txt", "text/plain", "Hello, World!".getBytes()
        );

        UploadResponse uploadResponse = storageService.uploadFile(file, testUserId);
        
        // Try to delete as different user
        assertThrows(SecurityException.class, () -> {
            storageService.deleteFile(uploadResponse.getFileId(), anotherUserId);
        });
    }

    @Test
    void testGetFileMetadata() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.txt", "text/plain", "Hello, World!".getBytes()
        );

        UploadResponse uploadResponse = storageService.uploadFile(file, testUserId);
        
        FileMetadata metadata = storageService.getFileMetadata(uploadResponse.getFileId(), testUserId);
        
        assertNotNull(metadata);
        assertEquals("test.txt", metadata.getFilename());
        assertEquals("text/plain", metadata.getContentType());
        assertEquals(13, metadata.getSize());
        assertEquals(testUserId, metadata.getOwnerId());
        assertNotNull(metadata.getChecksum());
        assertNotNull(metadata.getStoragePath());
    }

    @Test
    void testChecksumCalculation() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.txt", "text/plain", "Hello, World!".getBytes()
        );

        UploadResponse response1 = storageService.uploadFile(file, testUserId);
        UploadResponse response2 = storageService.uploadFile(file, testUserId);
        
        // Same content should have same checksum
        FileMetadata metadata1 = storageService.getFileMetadata(response1.getFileId(), testUserId);
        FileMetadata metadata2 = storageService.getFileMetadata(response2.getFileId(), testUserId);
        
        assertEquals(metadata1.getChecksum(), metadata2.getChecksum());
    }

    @Test
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/files/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.message").value("Storage Service"));
    }
}
