package com.example.eventlotterysystemapp;

import com.example.eventlotterysystemapp.data.models.User;
import com.example.eventlotterysystemapp.data.models.UserSession;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for UserSession.
 * Tests that the session correctly stores and retrieves the logged-in user.
 */
public class UserSessionTest {

    private User user1;
    private User user2;

    @Before
    public void setUp() {
        user1 = new User("John", "hash1", "john@gmail.com", "Entrant", "device1");
        user2 = new User("Jane", "hash2", "jane@gmail.com", "Organizer", "device2");
        UserSession.setUser(null); // reset before each test
    }

    @Test
    public void testSetAndGetUser_returnsSameInstance() {
        UserSession.setUser(user1);
        assertSame(user1, UserSession.getUser());
    }

    @Test
    public void testSetUser_initiallyNull() {
        assertNull(UserSession.getUser());
    }

    @Test
    public void testSetUser_toNull_returnsNull() {
        UserSession.setUser(user1);
        UserSession.setUser(null);
        assertNull(UserSession.getUser());
    }

    @Test
    public void testSetUser_updatesToNewUser_returnsNewUser() {
        UserSession.setUser(user1);
        UserSession.setUser(user2);
        assertSame(user2, UserSession.getUser());
    }

    @Test
    public void testSetUser_updatesToNewUser_doesNotReturnOldUser() {
        UserSession.setUser(user1);
        UserSession.setUser(user2);
        assertNotSame(user1, UserSession.getUser());
    }

    @Test
    public void testGetUser_nameIsCorrect() {
        UserSession.setUser(user1);
        assertEquals("John", UserSession.getUser().getName());
    }

    @Test
    public void testGetUser_emailIsCorrect() {
        UserSession.setUser(user1);
        assertEquals("john@gmail.com", UserSession.getUser().getEmail());
    }

    @Test
    public void testGetUser_roleIsCorrect() {
        UserSession.setUser(user1);
        assertEquals("Entrant", UserSession.getUser().getRole());
    }

    @Test
    public void testGetUser_afterSwitch_hasNewEmail() {
        UserSession.setUser(user1);
        UserSession.setUser(user2);
        assertEquals("jane@gmail.com", UserSession.getUser().getEmail());
    }
}
