package com.example.eventlotterysystemapp;

import com.example.eventlotterysystemapp.data.models.Participant;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Participant model.
 * US 01.01.01 - join waitlist
 * US 01.01.02 - leave waitlist
 * US 01.05.02 - accept invitation
 * US 01.05.03 - decline invitation
 * US 02.09.01 - co-organizer assignment
 */
public class ParticipantTest {

    private Participant participant;

    @Before
    public void setUp() {
        participant = new Participant("user@example.com", "waitlist");
    }

    // --- US 01.01.01: join waitlist ---

    @Test
    public void testParticipant_emailIsCorrect() {
        assertEquals("user@example.com", participant.getEmail());
    }

    @Test
    public void testParticipant_joinWaitlist_statusIsWaitlist() {
        assertEquals("waitlist", participant.getStatus());
    }

    // --- US 01.01.02: leave waitlist ---

    @Test
    public void testParticipant_leaveWaitlist_statusBecomescancelled() {
        participant.setStatus("cancelled");
        assertEquals("cancelled", participant.getStatus());
    }

    // --- US 01.05.02: accept invitation ---

    @Test
    public void testParticipant_acceptInvite_statusBecomesEnrolled() {
        participant.setStatus("selected");
        participant.setStatus("enrolled");
        assertEquals("enrolled", participant.getStatus());
    }

    // --- US 01.05.03: decline invitation ---

    @Test
    public void testParticipant_declineInvite_statusBecomesCancelled() {
        participant.setStatus("selected");
        participant.setStatus("cancelled");
        assertEquals("cancelled", participant.getStatus());
    }

    // --- Lottery selected status ---

    @Test
    public void testParticipant_lotteryWin_statusBecomesSelected() {
        participant.setStatus("selected");
        assertEquals("selected", participant.getStatus());
    }

    // --- US 02.09.01: co-organizer ---

    @Test
    public void testParticipant_coOrganizer_statusIsCorrect() {
        participant.setStatus("co-organizer");
        assertEquals("co-organizer", participant.getStatus());
    }

    // --- General setters ---

    @Test
    public void testParticipant_setEmail_updatesCorrectly() {
        participant.setEmail("new@example.com");
        assertEquals("new@example.com", participant.getEmail());
    }

    // --- Firestore requirement ---

    @Test
    public void testNoArgConstructor_doesNotThrow() {
        Participant p = new Participant();
        assertNotNull(p);
    }

    @Test
    public void testNoArgConstructor_emailIsNull() {
        Participant p = new Participant();
        assertNull(p.getEmail());
    }

    @Test
    public void testNoArgConstructor_statusIsNull() {
        Participant p = new Participant();
        assertNull(p.getStatus());
    }
}
