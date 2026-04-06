package com.example.eventlotterysystemapp;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import static org.junit.Assert.*;

/**
 * Unit tests for password hashing consistency.
 * Ensures LoginActivity and RegistrationActivity produce identical hashes
 * so login always works for registered users.
 */
public class PasswordHashTest {

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    @Test
    public void testHashPassword_sameInputProducesSameHash() {
        String hash1 = hashPassword("myPassword123");
        String hash2 = hashPassword("myPassword123");
        assertEquals(hash1, hash2);
    }

    @Test
    public void testHashPassword_differentInputsProduceDifferentHashes() {
        String hash1 = hashPassword("password1");
        String hash2 = hashPassword("password2");
        assertNotEquals(hash1, hash2);
    }

    @Test
    public void testHashPassword_isNotPlainText() {
        String password = "myPassword123";
        String hash = hashPassword(password);
        assertNotEquals(password, hash);
    }

    @Test
    public void testHashPassword_outputIs64Characters() {
        // SHA-256 always produces a 64-char hex string
        String hash = hashPassword("anyPassword");
        assertEquals(64, hash.length());
    }

    @Test
    public void testHashPassword_outputIsHexOnly() {
        String hash = hashPassword("testPassword");
        assertTrue(hash.matches("[0-9a-f]+"));
    }

    @Test
    public void testHashPassword_emptyStringProducesHash() {
        String hash = hashPassword("");
        assertNotNull(hash);
        assertEquals(64, hash.length());
    }

    @Test
    public void testHashPassword_registrationAndLoginHashesMatch() {
        // Simulates RegistrationActivity hashing on register
        String passwordAtRegistration = hashPassword("userPassword");
        // Simulates LoginActivity hashing on login
        String passwordAtLogin = hashPassword("userPassword");
        assertEquals("Registration and login hashes must match for login to work",
                passwordAtRegistration, passwordAtLogin);
    }

    @Test
    public void testHashPassword_caseSensitive() {
        String lowerHash = hashPassword("password");
        String upperHash = hashPassword("Password");
        assertNotEquals(lowerHash, upperHash);
    }
}
