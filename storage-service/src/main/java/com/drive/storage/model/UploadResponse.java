//========================================================================
// Upload
// ========================================================================

package com.drive.storage.model;

import java.time.Instant;
import java.util.UUID;

public class UploadResponse {
    private UUID fileId;
    private String fileName;
    private long size;
    private String contentType;
    private Instant uploadedAt;

    public UploadResponse(FileMetadata fileMetadata) {
        this.fileId = fileMetadata.getId();
        this.fileName = fileMetadata.getFilename();
        this.size = fileMetadata.getSize();
        this.contentType = fileMetadata.getContentType();
        this.uploadedAt = fileMetadata.getCreatedAt();
    }

    public UUID getFileId() { return fileId; }
    public String getFileName() { return fileName; }
    public long getSize() { return size; }
    public String getContentType() { return contentType; }
    public Instant getUploadedAt() { return uploadedAt; }
}
