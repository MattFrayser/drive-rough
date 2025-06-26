// ============================================================================
// File Operation REST endpoints
// ===========================================================================

package com.drive.storage.controller;

import com.drive.storage.model.*;
import com.drive.storage.service.StorageService;
import com.drive.storage.repository.InMemoryFileRepository;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.Map;

@RestController
@RequestMapping("/files")
public class StorageController {

    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }
    
    // Upload File
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                                     Authentication auth) {

        try {
            UUID userId = (UUID) auth.getPrincipal();
            UploadResponse response = storageService.uploadFile(file, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    // Download File
    @GetMapping("/{fileId}")
    public ResponseEntity<?> downloadFile(@PathVariable("fileId") UUID fileId,
                                          Authentication auth) {

        try {
            UUID userId = (UUID) auth.getPrincipal();
            InputStream file = storageService.downloadFile(fileId, userId);
            HttpHeaders headers = new HttpHeaders();
            FileMetadata metadata = storageService.getFileMetadata(fileId, userId);

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + metadata.getFilename() + "\"")
                .body(new InputStreamResource(file));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> listFiles(Authentication auth) {
        try {
            UUID userId = (UUID) auth.getPrincipal();
            List<FileInfo> files = storageService.listUserFiles(userId);
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));    
        }
    }
    
    // Info
    @GetMapping("/{fileId}/info")
    public ResponseEntity<?> getFileInfo(@PathVariable("fileId") UUID fileId,
                                         Authentication auth) {
        try {
            UUID userId = (UUID) auth.getPrincipal();
            FileMetadata metadata = storageService.getFileMetadata(fileId, userId);
            return ResponseEntity.ok(metadata);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Delte File
    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable("fileId") UUID fileId,
                                        Authentication auth) {
        try {
            UUID userId = (UUID) auth.getPrincipal();
            storageService.deleteFile(fileId, userId);
            return ResponseEntity.ok(Map.of("message", "File deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Health Check
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "message", "Storage Service"
        ));
    }

}
