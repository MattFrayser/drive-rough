package com.drive.auth.util;

import com.drive.auth.model.User;
import com.drive.auth.model.AuthRequest;

import java.util.UUID;

public class TestUsers {

    public static User sampleUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setPassword("password");
        return user;
    }

    public static AuthRequest sampleAuthRequest() {
        return new AuthRequest("testuser", "password");
    }

    public static AuthRequest validUser(String username) {
        return new AuthRequest(username, "password");
    }
}


