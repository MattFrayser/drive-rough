package com.drive.collab.service;

import com.drive.collab.model.CollaborationSession;
import java.util.List;
import java.util.Optional;

public interface CollaborationService {
    
    /**
     * Create a new collaboration session for a document
     */
    CollaborationSession createSession(String documentId, String ownerId);
    
    /**
     * Get session by document ID
     */
    Optional<CollaborationSession> getSession(String documentId);
    
    /**
     * Add participant to a session
     */
    void addParticipantToSession(String documentId, CollaborationSession.Participant participant);
    
    /**
     * Remove participant from session
     */
    void removeParticipantFromSession(String documentId, String sessionId);
    
    /**
     * Update participant activity
     */
    void updateParticipantActivity(String documentId, String sessionId);
    
    /**
     * Save document snapshot to storage service
     */
    void saveDocumentSnapshot(String documentId, byte[] encryptedSnapshot);
    
    /**
     * Get all active sessions
     */
    List<CollaborationSession> getActiveSessions();
    
    /**
     * Clean up inactive sessions
     */
    void cleanupInactiveSessions();
}
