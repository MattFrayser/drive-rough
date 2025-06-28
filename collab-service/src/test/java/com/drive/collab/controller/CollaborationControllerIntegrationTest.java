package com.drive.collab.controller;

import com.drive.collab.CollabServiceApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = CollabServiceApplication.class)
@AutoConfigureMockMvc
public class CollaborationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateAndGetSession() throws Exception {
        // Create session
        String docId = "test-doc-123";
        String ownerId = "11111111-1111-1111-1111-111111111111";
        String payload = "{\"documentId\":\"" + docId + "\",\"ownerId\":\"" + ownerId + "\"}";

        mockMvc.perform(post("/api/collab/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentId").value(docId));

        // Get session
        mockMvc.perform(get("/api/collab/sessions/" + docId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentId").value(docId));
    }
} 