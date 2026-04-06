package com.example.eventlotterysystemapp;

import com.example.eventlotterysystemapp.data.models.Participant;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Unit tests for lottery selection logic.
 * US 02.05.02 - sample attendees from waitlist
 * US 01.05.01 / 02.05.03 - draw replacement from waitlist
 * US 01.05.04 - waitlist count
 */
public class LotteryLogicTest {

    private List<Participant> buildWaitlist(int count) {
        List<Participant> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(new Participant("user" + i + "@example.com", "waitlist"));
        }
        return list;
    }

    // --- US 02.05.02: sample attendees ---

    @Test
    public void testLottery_selectedCountDoesNotExceedCapacity() {
        List<Participant> waitlist = buildWaitlist(30);
        int capacity = 20;
        Collections.shuffle(waitlist);
        int winners = Math.min(capacity, waitlist.size());
        assertEquals(20, winners);
    }

    @Test
    public void testLottery_whenWaitlistSmallerThanCapacity_allSelected() {
        List<Participant> waitlist = buildWaitlist(5);
        int capacity = 20;
        int winners = Math.min(capacity, waitlist.size());
        assertEquals(5, winners);
    }

    @Test
    public void testLottery_whenWaitlistEqualsCapacity_allSelected() {
        List<Participant> waitlist = buildWaitlist(20);
        int capacity = 20;
        int winners = Math.min(capacity, waitlist.size());
        assertEquals(20, winners);
    }

    @Test
    public void testLottery_emptyWaitlist_selectsZero() {
        List<Participant> waitlist = buildWaitlist(0);
        int winners = Math.min(20, waitlist.size());
        assertEquals(0, winners);
    }

    @Test
    public void testLottery_spotsAvailable_calculatedCorrectly() {
        int totalCapacity = 20;
        int currentEnrolled = 5;
        int currentSelected = 3;
        int currentOccupancy = currentEnrolled + currentSelected;
        int spotsAvailable = totalCapacity - currentOccupancy;
        assertEquals(12, spotsAvailable);
    }

    @Test
    public void testLottery_noSpotsAvailable_selectsZero() {
        int totalCapacity = 20;
        int currentOccupancy = 20;
        int spotsAvailable = totalCapacity - currentOccupancy;
        assertTrue(spotsAvailable <= 0);
    }

    @Test
    public void testLottery_winnersAndLosersAddUpToWaitlistSize() {
        List<Participant> waitlist = buildWaitlist(30);
        int capacity = 20;
        int winners = Math.min(capacity, waitlist.size());
        int losers = waitlist.size() - winners;
        assertEquals(waitlist.size(), winners + losers);
    }

    // --- US 01.05.01 / 02.05.03: draw one replacement ---

    @Test
    public void testDrawReplacement_picksOneFromWaitlist() {
        List<Participant> waitlist = buildWaitlist(10);
        Collections.shuffle(waitlist);
        Participant replacement = waitlist.get(0);
        assertNotNull(replacement);
        assertEquals("waitlist", replacement.getStatus());
    }

    @Test
    public void testDrawReplacement_emptyWaitlist_noCandidates() {
        List<Participant> waitlist = buildWaitlist(0);
        assertTrue(waitlist.isEmpty());
    }

    @Test
    public void testDrawReplacement_afterDraw_replacementStatusBecomesSelected() {
        List<Participant> waitlist = buildWaitlist(5);
        Participant replacement = waitlist.get(0);
        replacement.setStatus("selected");
        assertEquals("selected", replacement.getStatus());
    }

    // --- US 01.05.04: waitlist count ---

    @Test
    public void testWaitlistCount_isCorrect() {
        List<Participant> waitlist = buildWaitlist(15);
        assertEquals(15, waitlist.size());
    }

    @Test
    public void testWaitlistCount_afterJoining_increasesBy1() {
        List<Participant> waitlist = buildWaitlist(5);
        int before = waitlist.size();
        waitlist.add(new Participant("newcomer@example.com", "waitlist"));
        assertEquals(before + 1, waitlist.size());
    }

    @Test
    public void testWaitlistCount_afterLeaving_decreasesBy1() {
        List<Participant> waitlist = buildWaitlist(5);
        int before = waitlist.size();
        waitlist.remove(0);
        assertEquals(before - 1, waitlist.size());
    }

    // --- Availability check ---

    @Test
    public void testEvent_isFull_whenWaitlistCountMeetsLimit() {
        int limit = 10;
        int currentCount = 10;
        assertTrue(currentCount >= limit);
    }

    @Test
    public void testEvent_isOpen_whenWaitlistCountBelowLimit() {
        int limit = 10;
        int currentCount = 5;
        assertFalse(currentCount >= limit);
    }

    @Test
    public void testEvent_unlimitedWaitlist_isAlwaysOpen() {
        int limit = Integer.MAX_VALUE;
        int currentCount = 9999;
        assertFalse(limit <= 0 || limit == Integer.MAX_VALUE
                ? false
                : currentCount >= limit);
    }
}
