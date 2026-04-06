package com.example.eventlotterysystemapp;

import com.example.eventlotterysystemapp.data.models.Event;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Unit tests for search and filter logic.
 * US 01.01.04 - filter by availability/capacity
 * US 01.01.05 - search by keyword
 * US 01.01.06 - keyword search with filtering combined
 */
public class FilterLogicTest {

    private List<Event> allEvents;

    private Event makeEvent(String name, String category, int listLimit) {
        return new Event(name, "desc", category, "location",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(),
                false, "org1", null, listLimit, 20, false);
    }

    @Before
    public void setUp() {
        allEvents = new ArrayList<>();
        allEvents.add(makeEvent("Swimming Lessons", "Sports", 10));
        allEvents.add(makeEvent("Piano Classes", "Music", 5));
        allEvents.add(makeEvent("Art Workshop", "Art", 0)); // unlimited
        allEvents.add(makeEvent("Tech Talk", "Tech", 20));
        allEvents.add(makeEvent("Swimming Advanced", "Sports", 8));
    }

    // --- US 01.01.05: search by keyword ---

    private List<Event> searchByKeyword(List<Event> events, String query) {
        List<Event> results = new ArrayList<>();
        for (Event e : events) {
            if (query.isEmpty() || (e.getName() != null &&
                    e.getName().toLowerCase().contains(query.toLowerCase()))) {
                results.add(e);
            }
        }
        return results;
    }

    @Test
    public void testSearch_emptyQuery_returnsAll() {
        List<Event> results = searchByKeyword(allEvents, "");
        assertEquals(5, results.size());
    }

    @Test
    public void testSearch_matchingKeyword_returnsCorrectEvents() {
        List<Event> results = searchByKeyword(allEvents, "swimming");
        assertEquals(2, results.size());
    }

    @Test
    public void testSearch_caseInsensitive_returnsCorrectEvents() {
        List<Event> results = searchByKeyword(allEvents, "PIANO");
        assertEquals(1, results.size());
    }

    @Test
    public void testSearch_noMatch_returnsEmpty() {
        List<Event> results = searchByKeyword(allEvents, "xyznothing");
        assertEquals(0, results.size());
    }

    @Test
    public void testSearch_partialMatch_returnsCorrectEvents() {
        List<Event> results = searchByKeyword(allEvents, "swim");
        assertEquals(2, results.size());
    }

    // --- US 01.01.04: filter by interest/category ---

    private List<Event> filterByInterest(List<Event> events, String interest) {
        if (interest == null || interest.equals("interests") || interest.equals("all")) {
            return new ArrayList<>(events);
        }
        List<Event> results = new ArrayList<>();
        for (Event e : events) {
            String category = e.getCategory() != null ? e.getCategory().toLowerCase() : "";
            String name = e.getName() != null ? e.getName().toLowerCase() : "";
            if (category.contains(interest.toLowerCase()) || name.contains(interest.toLowerCase())) {
                results.add(e);
            }
        }
        return results;
    }

    @Test
    public void testInterestFilter_allReturnsEverything() {
        List<Event> results = filterByInterest(allEvents, "all");
        assertEquals(5, results.size());
    }

    @Test
    public void testInterestFilter_sportsReturnsCorrectEvents() {
        List<Event> results = filterByInterest(allEvents, "Sports");
        assertEquals(2, results.size());
    }

    @Test
    public void testInterestFilter_musicReturnsCorrectEvents() {
        List<Event> results = filterByInterest(allEvents, "Music");
        assertEquals(1, results.size());
    }

    @Test
    public void testInterestFilter_noMatchReturnsEmpty() {
        List<Event> results = filterByInterest(allEvents, "Dance");
        assertEquals(0, results.size());
    }

    // --- US 01.01.04: filter by availability (open/full) ---

    private boolean isEventFull(Event event, int currentCount) {
        int limit = event.getListLimit();
        if (limit <= 0 || limit == Integer.MAX_VALUE) return false; // unlimited = never full
        return currentCount >= limit;
    }

    @Test
    public void testAvailability_fullEvent_isFull() {
        Event event = makeEvent("Full Event", "Sports", 10);
        assertTrue(isEventFull(event, 10));
    }

    @Test
    public void testAvailability_openEvent_isNotFull() {
        Event event = makeEvent("Open Event", "Sports", 10);
        assertFalse(isEventFull(event, 5));
    }

    @Test
    public void testAvailability_unlimitedEvent_isNeverFull() {
        Event event = makeEvent("Unlimited Event", "Sports", Integer.MAX_VALUE);
        assertFalse(isEventFull(event, 99999));
    }

    @Test
    public void testAvailability_zeroLimit_isNeverFull() {
        Event event = makeEvent("No Limit Event", "Sports", 0);
        assertFalse(isEventFull(event, 99999));
    }

    // --- US 01.01.06: search and filter combined ---

    @Test
    public void testCombined_searchThenFilterByInterest_stacksCorrectly() {
        // Search for "swimming" first
        List<Event> searchResults = searchByKeyword(allEvents, "swimming");
        assertEquals(2, searchResults.size());

        // Then filter by Sports interest
        List<Event> filtered = filterByInterest(searchResults, "Sports");
        assertEquals(2, filtered.size());
    }

    @Test
    public void testCombined_searchThenFilterByInterest_reducesResults() {
        // Search for "swimming" - gets 2 results
        List<Event> searchResults = searchByKeyword(allEvents, "swimming");
        // Filter by Music - should return 0 (swimming events are Sports not Music)
        List<Event> filtered = filterByInterest(searchResults, "Music");
        assertEquals(0, filtered.size());
    }

    @Test
    public void testCombined_filterDoesNotExpandBeyondSearch() {
        List<Event> searchResults = searchByKeyword(allEvents, "piano");
        assertEquals(1, searchResults.size());

        // Applying "all" interest filter should not add more results
        List<Event> filtered = filterByInterest(searchResults, "all");
        assertEquals(1, filtered.size());
    }

    @Test
    public void testCombined_emptySearchWithCategoryFilter() {
        // Empty search = all events, then filter by Tech
        List<Event> searchResults = searchByKeyword(allEvents, "");
        List<Event> filtered = filterByInterest(searchResults, "Tech");
        assertEquals(1, filtered.size());
        assertEquals("Tech Talk", filtered.get(0).getName());
    }
}
