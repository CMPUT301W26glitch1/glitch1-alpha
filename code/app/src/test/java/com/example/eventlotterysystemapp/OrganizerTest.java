package com.example.eventlotterysystemapp;

import com.example.eventlotterysystemapp.data.models.Event;
import org.junit.Test;
import java.time.LocalDateTime;
import static org.junit.Assert.*;

/**
 * Unit tests for organizer event creation.
 * US 02.01.01 - create public event
 * US 02.01.02 - create private event
 * US 02.01.04 - set registration period
 * US 02.02.03 - geolocation toggle
 */
public class OrganizerTest {

    // --- US 02.01.01: create public event ---

    @Test
    public void testCreateEvent_fieldsAreCorrect() {
        String title = "Ultimate Speed Race";
        String description = "Car Racing competition";
        String category = "Racing";
        String location = "Edmonton Racing Track";
        LocalDateTime eventTime = LocalDateTime.now();
        LocalDateTime regStart = LocalDateTime.now();
        LocalDateTime regEnd = LocalDateTime.now().plusDays(7);
        String organizerId = "organizer@gmail.com";
        boolean geoRequired = true;

        Event event = new Event(
                title, description, category, location,
                eventTime, regStart, regEnd,
                geoRequired, organizerId, null,
                50, 20, false
        );

        assertEquals(title, event.getName());
        assertEquals(description, event.getDescription());
        assertEquals(category, event.getCategory());
        assertEquals(location, event.getLocation());
        assertEquals(organizerId, event.getOrganizerId());
        assertFalse(event.isPrivate());
    }

    // --- US 02.01.02: private event ---

    @Test
    public void testCreatePrivateEvent_isPrivate() {
        Event event = new Event(
                "Private Event", "desc", "category", "location",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                false, "org123", null, 10, 10, true
        );
        assertTrue(event.isPrivate());
    }

    // --- US 02.02.03: geolocation ---

    @Test
    public void testGeoLocation_isEnabled() {
        Event event = new Event(
                "Geo Event", "desc", "category", "location",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                true, "org123", null, 20, 10, false
        );
        assertTrue(event.isGeolocationReq());
    }

    @Test
    public void testGeoLocation_isDisabled() {
        Event event = new Event(
                "No Geo Event", "desc", "category", "location",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                false, "org123", null, 20, 10, false
        );
        assertFalse(event.isGeolocationReq());
    }

    // --- US 02.01.04: registration period ---

    @Test
    public void testRegistrationTimeOrder_regEndIsAfterRegStart() {
        LocalDateTime regStart = LocalDateTime.now();
        LocalDateTime regEnd = LocalDateTime.now().plusDays(1);
        assertTrue(regEnd.isAfter(regStart));
    }

    @Test
    public void testRegistrationPeriod_regStartIsBeforeEventTime() {
        LocalDateTime regStart = LocalDateTime.now();
        LocalDateTime eventTime = LocalDateTime.now().plusDays(14);
        assertTrue(regStart.isBefore(eventTime));
    }

    // --- US 02.03.01: waitlist limit ---

    @Test
    public void testWaitlistLimit_isSetCorrectly() {
        Event event = new Event(
                "Limited Event", "desc", "category", "location",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                false, "org123", null, 30, 15, false
        );
        assertEquals(30, event.getListLimit());
        assertEquals(15, event.getMaxParticipants());
    }
}
