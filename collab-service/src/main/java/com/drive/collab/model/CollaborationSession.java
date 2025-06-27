package com.drive.collab.model;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CollaborationSession {
    private final String sessionId;
    private final String documentId;
    private final UUID ownerId;
    private final Set<Participant> participants;
    private final Instant createdAt;
    private Instant lastActivity;
    private boolean active;

    public CollaborationSession(String documentId, UUID ownerId) {
        this.sessionId = UUID.randomUUID().toString();
        this.documentId = documentId;
        this.ownerId = ownerId;
        this.participants = ConcurrentHashMap.newKeySet();
        this.createdAt = Instant.now();
        this.lastActivity = Instant.now();
        this.active = true;
    }

    // Getters
    public String getSessionId() { return sessionId; }
    public String getDocumentId() { return documentId; }
    public UUID getOwnerId() { return ownerId; }
    public Set<Participant> getParticipants() { return participants; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastActivity() { return lastActivity; }
    public boolean isActive() { return active; }

    // Methods
    public void addParticipant(Participant participant) {
        participants.add(participant);
        updateActivity();
    }

    public void removeParticipant(UUID userId) {
        participants.removeIf(p -> p.getUserId().equals(userId));
        updateActivity();
    }

    public void updateActivity() {
        this.lastActivity = Instant.now();
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public static class Participant {
        private final UUID userId;
        private final String username;
        private final String sessionId;
        private final Instant joinedAt;
        private Instant lastSeen;

        public Participant(UUID userId, String username, String sessionId) {
            this.userId = userId;
            this.username = username;
            this.sessionId = sessionId;
            this.joinedAt = Instant.now();
            this.lastSeen = Instant.now();
        }

        // Getters
        public UUID getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getSessionId() { return sessionId; }
        public Instant getJoinedAt() { return joinedAt; }
        public Instant getLastSeen() { return lastSeen; }

        // Methods
        public void updateLastSeen() {
            this.lastSeen = Instant.now();
        }
    }
} 