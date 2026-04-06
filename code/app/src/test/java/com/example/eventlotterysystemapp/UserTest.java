package com.example.eventlotterysystemapp;

import com.example.eventlotterysystemapp.data.models.User;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for User model.
 * US 01.02.01 - provide personal info
 * US 01.02.02 - update profile info
 * US 01.04.03 - opt out of notifications
 */
public class UserTest {

    private User user;

    @Before
    public void setUp() {
        user = new User("John Doe", "hashedpassword123", "john@example.com", "Entrant", "device123");
    }

    // --- US 01.02.01: provide personal info ---

    @Test
    public void testUserCreation_nameIsCorrect() {
        assertEquals("John Doe", user.getName());
    }

    @Test
    public void testUserCreation_emailIsCorrect() {
        assertEquals("john@example.com", user.getEmail());
    }

    @Test
    public void testUserCreation_roleIsCorrect() {
        assertEquals("Entrant", user.getRole());
    }

    @Test
    public void testUserCreation_deviceIdIsCorrect() {
        assertEquals("device123", user.getLastDeviceId());
    }

    @Test
    public void testUserCreation_phoneDefaultIsNegativeOne() {
        assertEquals("-1", user.getPhoneNumber());
    }

    // --- US 01.02.02: update profile info ---

    @Test
    public void testSetName_updatesCorrectly() {
        user.setName("Jane Doe");
        assertEquals("Jane Doe", user.getName());
    }

    @Test
    public void testSetEmail_updatesCorrectly() {
        user.setEmail("jane@example.com");
        assertEquals("jane@example.com", user.getEmail());
    }

    @Test
    public void testSetPhoneNumber_updatesCorrectly() {
        user.setPhoneNumber(5551234);
        assertEquals("5551234", user.getPhoneNumber());
    }

    @Test
    public void testSetRole_updatesCorrectly() {
        user.setRole("Organizer");
        assertEquals("Organizer", user.getRole());
    }

    @Test
    public void testSetLastDeviceId_updatesCorrectly() {
        user.setLastDeviceId("newDevice456");
        assertEquals("newDevice456", user.getLastDeviceId());
    }

    // --- US 01.04.03: opt out of notifications ---

    @Test
    public void testNotificationsOptedOut_defaultIsFalse() {
        assertFalse(user.isNotificationsOptedOut());
    }

    @Test
    public void testSetNotificationsOptedOut_toTrue() {
        user.setNotificationsOptedOut(true);
        assertTrue(user.isNotificationsOptedOut());
    }

    @Test
    public void testSetNotificationsOptedOut_backToFalse() {
        user.setNotificationsOptedOut(true);
        user.setNotificationsOptedOut(false);
        assertFalse(user.isNotificationsOptedOut());
    }

    // --- Firestore requirement ---

    @Test
    public void testNoArgConstructor_doesNotThrow() {
        User emptyUser = new User();
        assertNotNull(emptyUser);
    }
}
