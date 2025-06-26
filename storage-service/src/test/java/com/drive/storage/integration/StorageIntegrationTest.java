package com.drive.storage.integration;

import com.drive.storage.BaseIntegrationTest;
import com.drive.storage.model.UploadResponse;
import com.drive.storage.util.JwtTestUtil;
import com.drive.storage.util.TestFileFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class StorageIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void uploadFile_ShouldSucceed_WithValidToken() throws Exception {
        UUID userId = UUID.randomUUID();
        String token = JwtTestUtil.generateTestToken(userId, "testuser");
        MockMultipartFile file = TestFileFactory.createTextFile("upload.txt", "Upload content");

        mockMvc.perform(multipart("/files/upload")
                        .file(file)
                        .header("Authorization", JwtTestUtil.createAuthHeader(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileId").exists())
                .andExpect(jsonPath("$.fileName").value("upload.txt"))
                .andExpect(jsonPath("$.size").value(14));
    }

    @Test
    void uploadFile_ShouldFail_WithoutToken() throws Exception {
        MockMultipartFile file = TestFileFactory.createTextFile("upload.txt", "Content");

        mockMvc.perform(multipart("/files/upload").file(file))
                .andExpect(status().isForbidden());
    }

    @Test
    void uploadFile_ShouldFail_WithInvalidToken() throws Exception {
        MockMultipartFile file = TestFileFactory.createTextFile("upload.txt", "Content");

        mockMvc.perform(multipart("/files/upload")
                        .file(file)
                        .header("Authorization", JwtTestUtil.createAuthHeader("invalid.token")))
                .andExpect(status().isForbidden());
    }

    @Test
    void uploadFile_ShouldFail_WithEmptyFile() throws Exception {
        UUID userId = UUID.randomUUID();
        String token = JwtTestUtil.generateTestToken(userId, "testuser");
        MockMultipartFile emptyFile = TestFileFactory.createEmptyFile();

        mockMvc.perform(multipart("/files/upload")
                        .file(emptyFile)
                        .header("Authorization", JwtTestUtil.createAuthHeader(token)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("File cannot be empty"));
    }

    @Test
    void listFiles_ShouldReturnUserFiles() throws Exception {
        UUID userId = UUID.randomUUID();
        String token = JwtTestUtil.generateTestToken(userId, "testuser");

        // Upload a file first
        MockMultipartFile file = TestFileFactory.createTextFile("list-test.txt", "List content");
        mockMvc.perform(multipart("/files/upload")
                        .file(file)
                        .header("Authorization", JwtTestUtil.createAuthHeader(token)))
                .andExpect(status().isOk());

        // List files
        mockMvc.perform(get("/files")
                        .header("Authorization", JwtTestUtil.createAuthHeader(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].filename").value("list-test.txt"));
    }

    @Test
    void downloadFile_ShouldSucceed_ForFileOwner() throws Exception {
        UUID userId = UUID.randomUUID();
        String token = JwtTestUtil.generateTestToken(userId, "testuser");
        String content = "Download this content";

        // Upload file
        MockMultipartFile file = TestFileFactory.createTextFile("download.txt", content);
        String uploadResponse = mockMvc.perform(multipart("/files/upload")
                        .file(file)
                        .header("Authorization", JwtTestUtil.createAuthHeader(token)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Extract fileId from response
        UploadResponse parsedResponse = objectMapper.readValue(uploadResponse, UploadResponse.class);
        String fileId = parsedResponse.getFileId().toString();

        // Download file
        mockMvc.perform(get("/files/" + fileId)
                        .header("Authorization", JwtTestUtil.createAuthHeader(token)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"download.txt\""))
                .andExpect(content().string(content));
    }

    @Test
    void downloadFile_ShouldFail_ForNonOwner() throws Exception {
        UUID ownerUserId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        String ownerToken = JwtTestUtil.generateTestToken(ownerUserId, "owner");
        String otherToken = JwtTestUtil.generateTestToken(otherUserId, "other");

        // Upload file as owner
        MockMultipartFile file = TestFileFactory.createTextFile("private.txt", "Private content");
        String uploadResponse = mockMvc.perform(multipart("/files/upload")
                        .file(file)
                        .header("Authorization", JwtTestUtil.createAuthHeader(ownerToken)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UploadResponse parsedResponse = objectMapper.readValue(uploadResponse, UploadResponse.class);
        String fileId = parsedResponse.getFileId().toString();

        // Try to download as different user
        mockMvc.perform(get("/files/" + fileId)
                        .header("Authorization", JwtTestUtil.createAuthHeader(otherToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("You do not own this file"));
    }

    @Test
    void deleteFile_ShouldSucceed_ForFileOwner() throws Exception {
        UUID userId = UUID.randomUUID();
        String token = JwtTestUtil.generateTestToken(userId, "testuser");

        // Upload file
        MockMultipartFile file = TestFileFactory.createTextFile("delete-me.txt", "Delete this");
        String uploadResponse = mockMvc.perform(multipart("/files/upload")
                        .file(file)
                        .header("Authorization", JwtTestUtil.createAuthHeader(token)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UploadResponse parsedResponse = objectMapper.readValue(uploadResponse, UploadResponse.class);
        String fileId = parsedResponse.getFileId().toString();

        // Delete file
        mockMvc.perform(delete("/files/" + fileId)
                        .header("Authorization", JwtTestUtil.createAuthHeader(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("File deleted successfully"));

        // Verify file is deleted
        mockMvc.perform(get("/files/" + fileId)
                        .header("Authorization", JwtTestUtil.createAuthHeader(token)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getFileInfo_ShouldReturnMetadata() throws Exception {
        UUID userId = UUID.randomUUID();
        String token = JwtTestUtil.generateTestToken(userId, "testuser");

        // Upload file
        MockMultipartFile file = TestFileFactory.createTextFile("info-test.txt", "Info content");
        String uploadResponse = mockMvc.perform(multipart("/files/upload")
                        .file(file)
                        .header("Authorization", JwtTestUtil.createAuthHeader(token)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UploadResponse parsedResponse = objectMapper.readValue(uploadResponse, UploadResponse.class);
        String fileId = parsedResponse.getFileId().toString();

        // Get file info
        mockMvc.perform(get("/files/" + fileId + "/info")
                        .header("Authorization", JwtTestUtil.createAuthHeader(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filename").value("info-test.txt"))
                .andExpect(jsonPath("$.contentType").value("text/plain"))
                .andExpect(jsonPath("$.size").value(12));
    }

    @Test
    void healthCheck_ShouldReturnUp() throws Exception {
        mockMvc.perform(get("/files/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.message").value("Storage Service"));
    }
}
