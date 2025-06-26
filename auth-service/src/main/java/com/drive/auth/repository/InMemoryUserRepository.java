// ============================================================================
// In memory Repo for testing
// ============================================================================

package com.drive.auth.repository;

import com.drive.auth.model.User;
import java.util.HashMap;
import java.util.Optional;

import org.springframework.stereotype.Component;


@Component
public class InMemoryUserRepository {
    private final HashMap<String, User> users = new HashMap<>();

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

    public void save(User user) {
        users.put(user.getUsername(), user);
    }
}
