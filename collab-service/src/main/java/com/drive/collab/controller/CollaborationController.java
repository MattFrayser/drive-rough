//=============================================================================
// Core Service Structure
//=============================================================================

package com.drive.collab.controller;

import com.drive.collab.model.CollaborationSession;
import com.drive.collab.service.CollaborationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/collab")
public class CollaborationController {

    @Autowired
    private CollaborationService collaborationService;

    @PostMapping("/sessions")
    public ResponseEntity<CollaborationSession> createSession(@RequestBody CreateSessionRequest request) {
        CollaborationSession session = collaborationService.createSession(request.getDocumentId(), request.getOwnerId());
        return ResponseEntity.ok(session);
    }

    @GetMapping("/sessions/{documentId}")
    public ResponseEntity<CollaborationSession> getSession(@PathVariable String documentId) {
        Optional<CollaborationSession> session = collaborationService.getSession(documentId);
        return session.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<CollaborationSession>> getActiveSessions() {
        List<CollaborationSession> sessions = collaborationService.getActiveSessions();
        return ResponseEntity.ok(sessions);
    }

    @DeleteMapping("/sessions/{documentId}")
    public ResponseEntity<Void> endSession(@PathVariable String documentId) {
        Optional<CollaborationSession> session = collaborationService.getSession(documentId);
        if (session.isPresent()) {
            session.get().setActive(false);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    public static class CreateSessionRequest {
        private String documentId;
        private String ownerId;

        public String getDocumentId() { return documentId; }
        public void setDocumentId(String documentId) { this.documentId = documentId; }

        public String getOwnerId() { return ownerId; }
        public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    }
}
