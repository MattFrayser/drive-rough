package com.drive.collab.websocket;

import com.drive.collab.model.CollaborationSession;
import com.drive.collab.service.CollaborationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class CollabWebSocketHandler {

    @Autowired
    private CollaborationService collaborationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/collab/{documentId}/join")
    @SendTo("/topic/collab/{documentId}/presence")
    public CollaborationSession.Participant handleJoin(@DestinationVariable String documentId,
                                                      @Payload JoinRequest joinRequest,
                                                      SimpMessageHeaderAccessor headerAccessor) {
        
        UUID userId = UUID.fromString(headerAccessor.getUser().getName());
        String sessionId = headerAccessor.getSessionId();
        
        CollaborationSession.Participant participant = new CollaborationSession.Participant(
            userId, joinRequest.getUsername(), sessionId);
        
        collaborationService.addParticipantToSession(documentId, participant);
        
        return participant;
    }

    @MessageMapping("/collab/{documentId}/update")
    @SendTo("/topic/collab/{documentId}")
    public byte[] handleUpdate(@DestinationVariable String documentId,
                              @Payload byte[] encryptedUpdate,
                              SimpMessageHeaderAccessor headerAccessor) {
        
        // Update participant's last seen
        String sessionId = headerAccessor.getSessionId();
        collaborationService.updateParticipantActivity(documentId, sessionId);
        
        // Broadcast the encrypted update to all participants
        return encryptedUpdate;
    }

    @MessageMapping("/collab/{documentId}/save")
    public void handleSave(@DestinationVariable String documentId,
                          @Payload SaveRequest saveRequest,
                          SimpMessageHeaderAccessor headerAccessor) {
        
        // Store encrypted snapshot to storage service
        collaborationService.saveDocumentSnapshot(documentId, saveRequest.getEncryptedSnapshot());
        
        // Notify all participants that document was saved
        messagingTemplate.convertAndSend("/topic/collab/" + documentId + "/saved", 
            new SaveNotification(saveRequest.getSavedBy()));
    }

    public static class JoinRequest {
        private String username;
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }

    public static class SaveRequest {
        private byte[] encryptedSnapshot;
        private String savedBy;
        
        public byte[] getEncryptedSnapshot() { return encryptedSnapshot; }
        public void setEncryptedSnapshot(byte[] encryptedSnapshot) { this.encryptedSnapshot = encryptedSnapshot; }
        
        public String getSavedBy() { return savedBy; }
        public void setSavedBy(String savedBy) { this.savedBy = savedBy; }
    }

    public static class SaveNotification {
        private final String savedBy;
        private final long timestamp;
        
        public SaveNotification(String savedBy) {
            this.savedBy = savedBy;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getSavedBy() { return savedBy; }
        public long getTimestamp() { return timestamp; }
    }
}
