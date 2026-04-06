package com.example.eventlotterysystemapp;

import static org.junit.Assert.*;

import com.example.eventlotterysystemapp.data.models.User;
import com.example.eventlotterysystemapp.data.models.UserSession;

import org.junit.Test;

public class UserSessionTest {

    @Test
    public void testSetAndGetUser() {
        User user = new User("John", "1234", "e@gmail.com", "entrant", "a");
        UserSession.setUser(user);
        assertSame(user, UserSession.getUser());
    }

    @Test
    public void testUserSessionReturnsSameInstance() {
        User user = new User("John", "1234", "e@gmail.com", "entrant", "a");
        UserSession.setUser(user);
        User returnedUser = UserSession.getUser();
        assertSame(user, returnedUser);
    }

    @Test
    public void testUserSessionUpdatesUser() {
        User user1 = new User("John", "1234", "e@gmail.com", "entrant", "a");
        User user2 = new User("Jane", "5678", "jane@gmail.com", "entrant", "b");

        UserSession.setUser(user1);
        assertSame(user1, UserSession.getUser());

        UserSession.setUser(user2);
        assertSame(user2, UserSession.getUser());
        assertNotSame(user1, UserSession.getUser());
    }

    @Test
    public void testUserSessionInitiallyNull() {
        UserSession.setUser(null);
        assertNull(UserSession.getUser());
    }
}