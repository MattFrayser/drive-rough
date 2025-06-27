package com.drive.collab.service;

import com.drive.collab.model.CollaborationSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class CollaborationServiceImpl implements CollaborationService {

    private final Map<String, CollaborationSession> sessions = new ConcurrentHashMap<>();
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public CollaborationSession createSession(String documentId, String ownerId) {
        CollaborationSession session = new CollaborationSession(documentId, UUID.fromString(ownerId));
        sessions.put(documentId, session);
        
        // Publish to Redis for multi-instance awareness
        redisTemplate.opsForValue().set("session:" + documentId, session);
        
        return session;
    }

    @Override
    public Optional<CollaborationSession> getSession(String documentId) {
        return Optional.ofNullable(sessions.get(documentId));
    }

    @Override
    public void addParticipantToSession(String documentId, CollaborationSession.Participant participant) {
        CollaborationSession session = sessions.get(documentId);
        if (session != null) {
            session.addParticipant(participant);
            
            // Publish presence update to Redis
            redisTemplate.convertAndSend("presence", 
                new PresenceEvent(documentId, "JOIN", participant));
        }
    }

    @Override
    public void removeParticipantFromSession(String documentId, String sessionId) {
        CollaborationSession session = sessions.get(documentId);
        if (session != null) {
            session.removeParticipant(UUID.fromString(sessionId));
            
            // Publish presence update to Redis
            redisTemplate.convertAndSend("presence", 
                new PresenceEvent(documentId, "LEAVE", sessionId));
        }
    }

    @Override
    public void updateParticipantActivity(String documentId, String sessionId) {
        CollaborationSession session = sessions.get(documentId);
        if (session != null) {
            session.getParticipants().stream()
                .filter(p -> p.getSessionId().equals(sessionId))
                .findFirst()
                .ifPresent(CollaborationSession.Participant::updateLastSeen);
        }
    }

    @Override
    public void saveDocumentSnapshot(String documentId, byte[] encryptedSnapshot) {
        // Call storage service to save the snapshot
        String storageUrl = "http://storage-service/api/files/" + documentId + "/snapshot";
        
        // Create request payload
        SnapshotRequest request = new SnapshotRequest();
        request.setEncryptedData(encryptedSnapshot);
        request.setDocumentId(documentId);
        request.setTimestamp(Instant.now());
        
        restTemplate.postForEntity(storageUrl, request, Void.class);
    }

    @Override
    public List<CollaborationSession> getActiveSessions() {
        return sessions.values().stream()
            .filter(CollaborationSession::isActive)
            .collect(Collectors.toList());
    }

    @Override
    public void cleanupInactiveSessions() {
        Instant cutoff = Instant.now().minusSeconds(3600); // 1 hour
        
        sessions.entrySet().removeIf(entry -> {
            CollaborationSession session = entry.getValue();
            if (session.getLastActivity().isBefore(cutoff)) {
                session.setActive(false);
                return true;
            }
            return false;
        });
    }

    // Helper classes for Redis messaging
    public static class PresenceEvent {
        private final String documentId;
        private final String eventType;
        private final Object data;
        
        public PresenceEvent(String documentId, String eventType, Object data) {
            this.documentId = documentId;
            this.eventType = eventType;
            this.data = data;
        }
        
        // Getters
        public String getDocumentId() { return documentId; }
        public String getEventType() { return eventType; }
        public Object getData() { return data; }
    }

    public static class SnapshotRequest {
        private byte[] encryptedData;
        private String documentId;
        private Instant timestamp;
        
        // Getters and setters
        public byte[] getEncryptedData() { return encryptedData; }
        public void setEncryptedData(byte[] encryptedData) { this.encryptedData = encryptedData; }
        
        public String getDocumentId() { return documentId; }
        public void setDocumentId(String documentId) { this.documentId = documentId; }
        
        public Instant getTimestamp() { return timestamp; }
        public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    }
} 