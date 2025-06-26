package com.drive.auth.service;

import com.drive.auth.model.AuthRequest;
import com.drive.auth.model.AuthResponse;
import com.drive.auth.model.User;
import com.drive.auth.repository.InMemoryUserRepository;
import com.drive.auth.util.TestUsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private InMemoryUserRepository userRepository;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        authService = new AuthService(userRepository);
    }

    @Test
    void register_ShouldCreateUserAndReturnResponse() throws Exception {
        AuthRequest request = TestUsers.validUser("matt");

        AuthResponse response = authService.register(request);

        assertNotNull(response.token);
        assertNotNull(response.publicKey);
        
        // Verify user was saved
        assertTrue(userRepository.findByUsername("matt").isPresent());
    }

    @Test
    void register_ShouldThrow_WhenUserExists() throws Exception {
        AuthRequest request = TestUsers.validUser("duplicate");
        
        // Register once
        authService.register(request);
        
        // Try to register again
        Exception exception = assertThrows(Exception.class, () -> {
            authService.register(request);
        });
        
        assertEquals("User already exists", exception.getMessage());
    }

    @Test
    void login_ShouldReturnResponse_WhenPasswordIsCorrect() throws Exception {
        // First register a user
        AuthRequest registerRequest = TestUsers.validUser("alice");
        authService.register(registerRequest);

        // Then try to login
        AuthRequest loginRequest = TestUsers.validUser("alice");
        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response.token);
        assertNotNull(response.publicKey);
    }

    @Test
    void login_ShouldThrow_WhenUserNotFound() {
        AuthRequest request = TestUsers.validUser("nonexistent");

        Exception exception = assertThrows(Exception.class, () -> {
            authService.login(request);
        });
        
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void login_ShouldThrow_WhenPasswordInvalid() throws Exception {
        // Register user with one password
        AuthRequest registerRequest = TestUsers.validUser("testuser");
        authService.register(registerRequest);
        
        // Try to login with wrong password
        AuthRequest badRequest = new AuthRequest("testuser", "wrongPassword");

        Exception exception = assertThrows(Exception.class, () -> {
            authService.login(badRequest);
        });
        
        assertEquals("Invalid password", exception.getMessage());
    }
}
