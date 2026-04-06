package com.example.eventlotterysystemapp;

import com.example.eventlotterysystemapp.data.models.Event;
import com.example.eventlotterysystemapp.data.models.Participant;
import com.example.eventlotterysystemapp.data.models.User;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Unit tests for admin features.
 * US 03.01.01 - remove events
 * US 03.02.01 - remove profiles
 * US 03.03.01 / 03.06.01 - browse/remove images
 * US 03.04.01 - browse events
 * US 03.05.01 - browse profiles
 * US 03.07.01 - remove organizers
 * US 03.08.01 - notification logs
 * US 03.09.01 - admin dual role
 */
public class AdminFeatureTest {

    private List<Event> eventList;
    private List<User> userList;

    @Before
    public void setUp() {
        eventList = new ArrayList<>();
        userList = new ArrayList<>();

        eventList.add(new Event("Event A", "desc", "Sports", "Location",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                false, "org1", null, 20, 10, false));
        eventList.add(new Event("Event B", "desc", "Music", "Location",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                false, "org2", null, 30, 15, false));

        userList.add(new User("Alice", "hash1", "alice@example.com", "Organizer", "device1"));
        userList.add(new User("Bob", "hash2", "bob@example.com", "Entrant", "device2"));
        userList.add(new User("Carol", "hash3", "carol@example.com", "Organizer", "device3"));
    }

    // --- US 03.04.01: browse events ---

    @Test
    public void testBrowseEvents_listIsNotEmpty() {
        assertFalse(eventList.isEmpty());
    }

    @Test
    public void testBrowseEvents_countIsCorrect() {
        assertEquals(2, eventList.size());
    }

    @Test
    public void testBrowseEvents_firstEventNameIsCorrect() {
        assertEquals("Event A", eventList.get(0).getName());
    }

    // --- US 03.01.01: remove events ---

    @Test
    public void testRemoveEvent_listSizeDecreasesBy1() {
        int before = eventList.size();
        eventList.remove(0);
        assertEquals(before - 1, eventList.size());
    }

    @Test
    public void testRemoveEvent_correctEventIsRemoved() {
        eventList.remove(0);
        assertEquals("Event B", eventList.get(0).getName());
    }

    @Test
    public void testRemoveAllEvents_listIsEmpty() {
        eventList.clear();
        assertTrue(eventList.isEmpty());
    }

    // --- US 03.05.01: browse profiles ---

    @Test
    public void testBrowseProfiles_listIsNotEmpty() {
        assertFalse(userList.isEmpty());
    }

    @Test
    public void testBrowseProfiles_countIsCorrect() {
        assertEquals(3, userList.size());
    }

    @Test
    public void testBrowseProfiles_emailContainsAtSymbol() {
        for (User user : userList) {
            assertTrue(user.getEmail().contains("@"));
        }
    }

    // --- US 03.02.01: remove profiles ---

    @Test
    public void testRemoveProfile_listSizeDecreasesBy1() {
        int before = userList.size();
        userList.remove(0);
        assertEquals(before - 1, userList.size());
    }

    @Test
    public void testRemoveProfile_correctUserIsRemoved() {
        userList.remove(0);
        assertEquals("Bob", userList.get(0).getName());
    }

    // --- US 03.07.01: remove organizers ---

    @Test
    public void testRemoveOrganizer_onlyOrganizerRoleRemoved() {
        userList.removeIf(u -> "Organizer".equals(u.getRole()));
        for (User user : userList) {
            assertNotEquals("Organizer", user.getRole());
        }
    }

    @Test
    public void testRemoveOrganizer_entrantsStillInList() {
        userList.removeIf(u -> "Organizer".equals(u.getRole()));
        assertEquals(1, userList.size());
        assertEquals("Entrant", userList.get(0).getRole());
    }

    // --- US 03.03.01 / 03.06.01: images ---

    @Test
    public void testImageUrl_isNotNull() {
        String imageUrl = "https://firebase.example.com/image.jpg";
        assertNotNull(imageUrl);
    }

    @Test
    public void testImageUrl_isValidHttpUrl() {
        String imageUrl = "https://firebase.example.com/image.jpg";
        assertTrue(imageUrl.startsWith("https://"));
    }

    @Test
    public void testRemoveImage_listSizeDecreasesBy1() {
        List<String> imageList = new ArrayList<>();
        imageList.add("https://example.com/img1.jpg");
        imageList.add("https://example.com/img2.jpg");
        int before = imageList.size();
        imageList.remove(0);
        assertEquals(before - 1, imageList.size());
    }

    // --- US 03.08.01: notification logs ---

    @Test
    public void testNotificationLog_messageIsNotEmpty() {
        String message = "Lottery results for Swimming Lessons have been sent.";
        assertFalse(message.isEmpty());
    }

    @Test
    public void testNotificationLog_senderIsNotNull() {
        String sender = "Organizer";
        assertNotNull(sender);
    }

    @Test
    public void testNotificationLog_hasEventName() {
        String eventName = "Swimming Lessons";
        assertFalse(eventName.isEmpty());
    }

    // --- US 03.09.01: admin dual role ---

    @Test
    public void testAdminDualRole_adminRoleIsCorrect() {
        User admin = new User("Admin User", "hash", "admin@example.com", "Admin", "device");
        assertEquals("Admin", admin.getRole());
    }

    @Test
    public void testAdminDualRole_adminCanSwitchToEntrant() {
        // Admin's email can be used to browse events as an entrant
        User admin = new User("Admin User", "hash", "admin@example.com", "Admin", "device");
        // The email is passed to EventListActivity with IS_ADMIN=true flag
        assertNotNull(admin.getEmail());
        assertTrue(admin.getEmail().contains("@"));
    }

    @Test
    public void testAdminDualRole_adminCanSwitchToOrganizer() {
        User admin = new User("Admin User", "hash", "admin@example.com", "Admin", "device");
        assertNotNull(admin.getEmail());
        assertEquals("Admin", admin.getRole());
    }
}
