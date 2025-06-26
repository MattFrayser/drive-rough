// ============================================================================
// File listing
// ============================================================================

package com.drive.storage.model;

import java.time.Instant;
import java.util.UUID;

public class FileInfo {
    private UUID fileId;    
    private String filename;
    private long size;
    private String contentType;
    private Instant createdAt;
    private Instant updatedAt;

    public FileInfo(FileMetadata metadata) {
        this.fileId = metadata.getId();
        this.filename = metadata.getFilename();
        this.size = metadata.getSize();
        this.contentType = metadata.getContentType();
        this.createdAt = metadata.getCreatedAt();
        this.updatedAt = metadata.getUpdatedAt();
    }

    public UUID getFileId() { return fileId; }
    public void setFileId(UUID fileId) { this.fileId = fileId; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
