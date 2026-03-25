package com.example.eventlotterysystemapp;

import static org.junit.Assert.*;

import com.example.eventlotterysystemapp.data.models.User;
import com.example.eventlotterysystemapp.data.models.UserSession;

import org.junit.Test;

public class UserSessionTest {

    @Test
    public void testSetAndGetUser() {
        //sample
        User user = new User("John", "1234", "e@gmail.com", "entrant", "a");

        UserSession.setUser(user);

        assertEquals(user, UserSession.getUser());
    }

    @Test
    public void testUserSessionReturnsSameInstance() {
        //sample
        User user = new User("John", "1234", "e@gmail.com", "entrant", "a");

        UserSession.setUser(user);

        User returnedUser = UserSession.getUser();

        assertSame(user, returnedUser);
    }

    @Test
    public void testUserSessionUpdatesUser() {
        //sample users
        User user = new User("John", "1234", "e@gmail.com", "entrant", "a");
        User user2 = new User("Jogghn", "12gg34", "e33@gmail.com", "entrant", "a");

        UserSession.setUser(user);
        assertEquals(user2, UserSession.getUser());

        UserSession.setUser(user2);
        assertEquals(user2, UserSession.getUser());
    }

    @Test
    public void testUserSessionInitiallyNull() {
        UserSession.setUser(null);
        assertNull(UserSession.getUser());
    }
}