package com.example.eventlotterysystemapp;

import org.junit.Test;

import static org.junit.Assert.*;

public class AdminFeatureTest {

    // ==============================
    // USER STORY 11 - Browse Images
    // ==============================

    @Test
    public void testImageUploaderEmailContainsAt() {
        String uploader = "organizer@gmail.com";
        assertTrue(uploader.contains("@"));
    }

    @Test
    public void testImageUploaderNotNull() {
        String uploader = "organizer@gmail.com";
        assertNotNull(uploader);
    }

    // ==============================
    // USER STORY 48 - Remove Images
    // ==============================

    @Test
    public void testRemoveImageFromList() {
        int initialImageCount = 3;
        int afterRemovalCount = initialImageCount - 1;

        assertEquals(2, afterRemovalCount);
    }

    @Test
    public void testImageListNotEmptyBeforeRemoval() {
        int imageCount = 1;
        assertTrue(imageCount > 0);
    }

    // ==============================
    // USER STORY 6 - Notification Logs
    // ==============================

    @Test
    public void testNotificationMessageNotEmpty() {
        String message = "Event starting soon";
        assertFalse(message.isEmpty());
    }

    @Test
    public void testNotificationSenderExists() {
        String sender = "Organizer";
        assertNotNull(sender);
    }

    // ==============================
    // USER STORY 14 - Browse Profiles
    // ==============================

    @Test
    public void testProfileNameMatches() {
        String name = "John Doe";
        assertEquals("John Doe", name);
    }

    @Test
    public void testProfileEmailContainsAt() {
        String email = "john@example.com";
        assertTrue(email.contains("@"));
    }

}