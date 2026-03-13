package com.example.eventlotterysystemapp;

import com.example.eventlotterysystemapp.data.models.Event;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class OrganizerTest {
    @Test
    public void testCreateEvent() {
        String title = "Ultimate Speed Race";
        String description = "Car Racing competition";
        String category = "Racing";
        String location = "Edmonton Racing Track";

        LocalDateTime eventTime = LocalDateTime.now();
        LocalDateTime regStart = LocalDateTime.now();

        String id = "organizer@gmail.com";

        boolean geoRequired = true;

        Event event = new Event(
                title,
                description,
                category,
                location,
                eventTime,
                regStart,
                null,
                geoRequired,
                id,
                null
        );

        assertEquals(title, event.getName());
        assertEquals(description, event.getDescription());
        assertEquals(category, event.getCategory());
        assertEquals(location, event.getLocation());
        assertEquals(id, event.getOrganizerId());

    }

    @Test
    public void testGeoLocation(){
        boolean geoLocation = true;
        assertTrue(geoLocation);
    }

    @Test
    public void testRegistrationTimeOrder() {
        LocalDateTime regStart = LocalDateTime.now();
        LocalDateTime regEnd = LocalDateTime.now().plusDays(1);
        assertTrue(regEnd.isAfter(regStart));
    }
}
