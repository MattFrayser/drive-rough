//========================================================================
// Handle file operations with metadata mamagement
// ========================================================================

package com.drive.storage.service;

import com.drive.storage.backend.StorageBackend;
import com.drive.storage.model.*;
import com.drive.storage.repository.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StorageService {

    private final StorageBackend storageBackend;
    private final FileRepository fileRepository;

    public StorageService(StorageBackend storageBackend, FileRepository fileRepository) {
        this.storageBackend = storageBackend;
        this.fileRepository = fileRepository;
    }
    
    // Upload
    public UploadResponse uploadFile(MultipartFile file, UUID ownerId) throws Exception {
        
        // Check if file exists
        if (file.isEmpty())
            throw new IllegalArgumentException("File cannot be empty");
        
        // read file fileBytes
        byte[] fileBytes = file.getBytes();

        // Calc Checksum
        String checksum = calcChecksum(fileBytes);

        // Store file 
        String storagePath = storageBackend.store(
            new ByteArrayInputStream(fileBytes),
            file.getOriginalFilename()
        );

        // metadata
        FileMetadata metadata = new FileMetadata();
        metadata.setFilename(file.getOriginalFilename());
        metadata.setContentType(file.getContentType());
        metadata.setSize(file.getSize());
        metadata.setOwnerId(ownerId);
        metadata.setStoragePath(storagePath);
        metadata.setChecksum(checksum);

        // Save metadata
        metadata = fileRepository.save(metadata);

        // Return response
        return new UploadResponse(metadata);
    }

    // Download
    public InputStream downloadFile(UUID fileId, UUID userId) throws Exception {

        // Find file
        FileMetadata metadata = getFileMetadata(fileId, userId);

        return storageBackend.retrieve(metadata.getStoragePath());
    }


    // Return metadata of file
    public FileMetadata getFileMetadata(UUID fileId, UUID userId) throws Exception {

        FileMetadata metadata = fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
        
        // Check checkOwnership
        if (!metadata.getOwnerId().equals(userId))
            throw new SecurityException("You do not own this file");
        
        return metadata;
    }

    // List users files 
    public List<FileInfo> listUserFiles(UUID userId) {
        return fileRepository.findByOwnerId(userId).stream()
                .map(FileInfo::new)
                .collect(Collectors.toList());
    }

    // Delete file
    public void deleteFile(UUID fileId, UUID userId) throws Exception {
        FileMetadata metadata = getFileMetadata(fileId, userId);

        // Delete from storage  
        storageBackend.delete(metadata.getStoragePath());

        // Delete metadata
        fileRepository.deleteById(fileId);

    }

    // Checksum helper
    private String calcChecksum(byte[] fileBytes) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(fileBytes);
        
        StringBuilder sb = new StringBuilder();
        for (byte hashByte : hashBytes) {
            sb.append(String.format("%02x", hashByte));
        }
        return sb.toString();
    }

}
    


        

            

