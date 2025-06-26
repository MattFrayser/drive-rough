// ============================================================================
// Central unit for auth
// Uses: Register, Login, Gen keypairs, Hash & Verify pass, Issue tokens
// ============================================================================

// ============================================================================
// Central unit for auth
// Uses: Register, Login, Gen keypairs, Hash & Verify pass, Issue tokens
// ============================================================================

package com.drive.auth.service;

import com.drive.auth.model.*;
import com.drive.auth.repository.InMemoryUserRepository;
import com.drive.auth.security.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.security.*;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;  

@Service
public class AuthService {
    private final InMemoryUserRepository repo;

    public AuthService(InMemoryUserRepository repo) {
        this.repo = repo;
    }

    public AuthResponse register(AuthRequest request) throws Exception {
        if (repo.findByUsername(request.getUsername()).isPresent()) {
            throw new Exception("User already exists");
        }

        // Hash pass
        String passwordHash = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

        // Generate RSA keypair
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.genKeyPair();

        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

        String encryptedPrivateKey = encryptWithPassword(privateKey, request.getPassword());

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(request.getUsername());
        user.setPassword(passwordHash);
        user.setPublicKey(publicKey);
        user.setEncryptedPrivateKey(encryptedPrivateKey);
        repo.save(user);

        String token = JwtUtil.generateToken(user);
        return new AuthResponse(token, publicKey);
    }

    public AuthResponse login(AuthRequest request) throws Exception {
        User user = repo.findByUsername(request.getUsername())
            .orElseThrow(() -> new Exception("User not found"));

        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) 
            throw new Exception("Invalid password");

        String token = JwtUtil.generateToken(user);
        return new AuthResponse(token, user.getPublicKey());
    }

    private String encryptWithPassword(String privateKey, String password) {
        try {
            // Generate a proper AES key from password
            byte[] salt = "fixedsalt123".getBytes(); // For now, use fixed salt (improve later)
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 1000, 256);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();
            
            // Create AES key
            javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(keyBytes, "AES");
            
            // Encrypt
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(privateKey.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting private key: " + e.getMessage(), e);
        }
    }
}
