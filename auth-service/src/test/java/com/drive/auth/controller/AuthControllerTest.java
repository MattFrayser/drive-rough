package com.drive.auth.controller;

import com.drive.auth.BaseIntegrationTest;
import com.drive.auth.model.AuthRequest;
import com.drive.auth.util.JsonUtil;
import com.drive.auth.util.TestUsers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AuthControllerTest extends BaseIntegrationTest {

    @Test
    void register_ShouldReturnTokenAndPublicKey() throws Exception {
        AuthRequest req = TestUsers.validUser("testuser");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.publicKey").exists());
   }

    @Test
    void login_ShouldSucceedAfterRegistering() throws Exception {
        AuthRequest req = TestUsers.validUser("loginuser");

        // First register
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(req)))
                .andExpect(status().isOk());

        // Then login
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.publicKey").exists());
    }
}

