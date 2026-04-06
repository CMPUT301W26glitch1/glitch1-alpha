package com.example.eventlotterysystemapp;

import com.example.eventlotterysystemapp.data.models.Event;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDateTime;
import static org.junit.Assert.*;

/**
 * Unit tests for Event model.
 * US 02.01.01 - create public event
 * US 02.01.02 - create private event
 * US 02.01.04 - set registration period
 * US 02.02.03 - geolocation toggle
 * US 02.03.01 - limit waitlist size
 * US 02.04.01 - upload poster
 */
public class EventTest {

    private Event publicEvent;
    private Event privateEvent;

    @Before
    public void setUp() {
        LocalDateTime eventTime = LocalDateTime.of(2025, 6, 15, 10, 0);
        LocalDateTime regStart  = LocalDateTime.of(2025, 6, 1, 0, 0);
        LocalDateTime regEnd    = LocalDateTime.of(2025, 6, 10, 23, 59);

        publicEvent = new Event(
                "Swimming Lessons", "Beginner swimming", "Sports",
                "Community Centre Pool", eventTime, regStart, regEnd,
                false, "organizer123", null, 50, 20, false
        );

        privateEvent = new Event(
                "Private Dance Class", "Invite only", "Dance",
                "Studio A", eventTime, regStart, regEnd,
                false, "organizer456", null, 10, 10, true
        );
    }

    // --- US 02.01.01: create public event ---

    @Test
    public void testPublicEvent_isNotPrivate() {
        assertFalse(publicEvent.isPrivate());
    }

    @Test
    public void testPublicEvent_nameIsCorrect() {
        assertEquals("Swimming Lessons", publicEvent.getName());
    }

    @Test
    public void testPublicEvent_categoryIsCorrect() {
        assertEquals("Sports", publicEvent.getCategory());
    }

    @Test
    public void testPublicEvent_locationIsCorrect() {
        assertEquals("Community Centre Pool", publicEvent.getLocation());
    }

    @Test
    public void testPublicEvent_organizerIdIsCorrect() {
        assertEquals("organizer123", publicEvent.getOrganizerId());
    }

    @Test
    public void testPublicEvent_descriptionIsCorrect() {
        assertEquals("Beginner swimming", publicEvent.getDescription());
    }

    // --- US 02.01.02: private event ---

    @Test
    public void testPrivateEvent_isPrivate() {
        assertTrue(privateEvent.isPrivate());
    }

    @Test
    public void testSetPrivate_toTrue() {
        publicEvent.setPrivate(true);
        assertTrue(publicEvent.isPrivate());
    }

    @Test
    public void testSetPrivate_toFalse() {
        privateEvent.setPrivate(false);
        assertFalse(privateEvent.isPrivate());
    }

    // --- US 02.01.04: registration period ---

    @Test
    public void testEvent_hasRegStartDate() {
        assertNotNull(publicEvent.getRegStartAsDate());
    }

    @Test
    public void testEvent_hasRegEndDate() {
        assertNotNull(publicEvent.getRegEndAsDate());
    }

    @Test
    public void testEvent_hasEventDateTime() {
        assertNotNull(publicEvent.getDateTimeAsDate());
    }

    @Test
    public void testEvent_regStartIsBeforeRegEnd() {
        assertTrue(publicEvent.getRegStartAsDate().before(publicEvent.getRegEndAsDate()));
    }

    // --- US 02.02.03: geolocation toggle ---

    @Test
    public void testEvent_geolocationDefaultFalse() {
        assertFalse(publicEvent.isGeolocationReq());
    }

    @Test
    public void testEvent_setGeolocationTrue() {
        publicEvent.setGeolocationReq(true);
        assertTrue(publicEvent.isGeolocationReq());
    }

    @Test
    public void testEvent_setGeolocationFalse() {
        publicEvent.setGeolocationReq(true);
        publicEvent.setGeolocationReq(false);
        assertFalse(publicEvent.isGeolocationReq());
    }

    // --- US 02.03.01: limit waitlist size ---

    @Test
    public void testEvent_listLimitIsCorrect() {
        assertEquals(50, publicEvent.getListLimit());
    }

    @Test
    public void testEvent_setListLimit() {
        publicEvent.setListLimit(100);
        assertEquals(100, publicEvent.getListLimit());
    }

    @Test
    public void testEvent_maxParticipantsIsCorrect() {
        assertEquals(20, publicEvent.getMaxParticipants());
    }

    @Test
    public void testEvent_setMaxParticipants() {
        publicEvent.setMaxParticipants(30);
        assertEquals(30, publicEvent.getMaxParticipants());
    }

    @Test
    public void testEvent_listLimitGreaterThanOrEqualToMaxParticipants() {
        assertTrue(publicEvent.getListLimit() >= publicEvent.getMaxParticipants());
    }

    // --- US 02.04.01: poster URL ---

    @Test
    public void testEvent_posterUrlNullByDefault() {
        assertNull(publicEvent.getPosterUrl());
    }

    @Test
    public void testEvent_setPosterUrl() {
        publicEvent.setPosterUrl("https://example.com/poster.jpg");
        assertEquals("https://example.com/poster.jpg", publicEvent.getPosterUrl());
    }

    // --- US 02.04.02: update poster ---

    @Test
    public void testEvent_updatePosterUrl() {
        publicEvent.setPosterUrl("https://example.com/old.jpg");
        publicEvent.setPosterUrl("https://example.com/new.jpg");
        assertEquals("https://example.com/new.jpg", publicEvent.getPosterUrl());
    }

    // --- Firestore requirement ---

    @Test
    public void testNoArgConstructor_doesNotThrow() {
        Event e = new Event();
        assertNotNull(e);
    }

    @Test
    public void testSetEventId_updatesCorrectly() {
        publicEvent.setEventId("event123");
        assertEquals("event123", publicEvent.getEventId());
    }
}
