package com.example.eventlotterysystemapp;

import com.example.eventlotterysystemapp.data.models.Notification;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Notification model.
 * US 01.04.01 - notified when chosen (LOTTERY_WIN)
 * US 01.04.02 - notified when not chosen (LOTTERY_LOSS)
 * US 01.05.02 - accept invitation
 * US 01.05.03 - decline invitation
 * US 01.05.06 - private event invite notification
 * US 01.09.01 - co-organizer invite notification
 */
public class NotificationModelTest {

    private Notification notification;

    @Before
    public void setUp() {
        notification = new Notification();
        notification.setId("notif123");
        notification.setRecipientEmail("user@example.com");
        notification.setRecipientId("user123");
        notification.setEventId("event456");
        notification.setMessage("You won the lottery!");
        notification.setType("LOTTERY_WIN");
        notification.setStatus("pending");
    }

    // --- US 01.04.01: lottery win notification ---

    @Test
    public void testNotification_lotteryWinType_isCorrect() {
        assertEquals("LOTTERY_WIN", notification.getType());
    }

    @Test
    public void testNotification_messageIsCorrect() {
        assertEquals("You won the lottery!", notification.getMessage());
    }

    @Test
    public void testNotification_recipientEmailIsCorrect() {
        assertEquals("user@example.com", notification.getRecipientEmail());
    }

    @Test
    public void testNotification_eventIdIsCorrect() {
        assertEquals("event456", notification.getEventId());
    }

    // --- US 01.04.02: lottery loss notification ---

    @Test
    public void testNotification_lotteryLossType_isCorrect() {
        notification.setType("LOTTERY_LOSS");
        assertEquals("LOTTERY_LOSS", notification.getType());
    }

    // --- Pending status means Accept/Decline buttons should show ---

    @Test
    public void testNotification_defaultStatusIsPending() {
        assertEquals("pending", notification.getStatus());
    }

    @Test
    public void testNotification_lotteryWinIsPending_shouldShowButtons() {
        assertTrue("LOTTERY_WIN".equals(notification.getType()) &&
                "pending".equals(notification.getStatus()));
    }

    // --- US 01.05.02: accept invitation ---

    @Test
    public void testNotification_acceptedStatus_isCorrect() {
        notification.setStatus("accepted");
        assertEquals("accepted", notification.getStatus());
    }

    @Test
    public void testNotification_accepted_shouldNotShowButtons() {
        notification.setStatus("accepted");
        assertFalse("pending".equals(notification.getStatus()));
    }

    // --- US 01.05.03: decline invitation ---

    @Test
    public void testNotification_declinedStatus_isCorrect() {
        notification.setStatus("declined");
        assertEquals("declined", notification.getStatus());
    }

    // --- US 01.05.06: private event invite ---

    @Test
    public void testNotification_privateInviteType_isCorrect() {
        notification.setType("PRIVATE_INVITE");
        assertEquals("PRIVATE_INVITE", notification.getType());
    }

    @Test
    public void testNotification_privateInvitePending_shouldShowButtons() {
        notification.setType("PRIVATE_INVITE");
        assertTrue("PRIVATE_INVITE".equals(notification.getType()) &&
                "pending".equals(notification.getStatus()));
    }

    // --- US 01.09.01: co-organizer invite ---

    @Test
    public void testNotification_coOrgInviteType_isCorrect() {
        notification.setType("COORG_INVITE");
        assertEquals("COORG_INVITE", notification.getType());
    }

    @Test
    public void testNotification_coOrgInvitePending_shouldShowButtons() {
        notification.setType("COORG_INVITE");
        assertTrue("COORG_INVITE".equals(notification.getType()) &&
                "pending".equals(notification.getStatus()));
    }

    // --- Setters ---

    @Test
    public void testNotification_setId_updatesCorrectly() {
        notification.setId("newId");
        assertEquals("newId", notification.getId());
    }

    @Test
    public void testNotification_setRecipientId_updatesCorrectly() {
        notification.setRecipientId("newUserId");
        assertEquals("newUserId", notification.getRecipientId());
    }

    // --- Firestore requirement ---

    @Test
    public void testNoArgConstructor_doesNotThrow() {
        Notification n = new Notification();
        assertNotNull(n);
    }
}
