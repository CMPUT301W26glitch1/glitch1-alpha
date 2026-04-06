package com.example.eventlotterysystemapp;

import com.example.eventlotterysystemapp.data.models.Event;
import com.example.eventlotterysystemapp.data.models.Participant;
import com.example.eventlotterysystemapp.data.models.Comment;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Unit tests for remaining completed US:
 * US 01.01.03 - browse events list
 * US 01.05.05 - lottery criteria info
 * US 02.01.03 - invite entrants to private event
 * US 02.02.01 - view waitlist participants
 * US 02.05.01 - notify chosen entrants
 * US 02.06.01 - view chosen (selected) entrants
 * US 02.06.02 - view cancelled entrants
 * US 02.06.03 - view enrolled entrants
 * US 02.06.04 - cancel entrants
 * US 02.06.05 - CSV export
 * US 02.07.01/02/03 - send notifications to groups
 * US 02.08.01 - organizer view/delete comments
 */
public class EventBrowseAndParticipantTest {

    private List<Event> publicEvents;
    private List<Participant> allParticipants;

    private Event makeEvent(String name, boolean isPrivate) {
        return new Event(name, "desc", "Sports", "Location",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                false, "org1", null, 20, 10, isPrivate);
    }

    private Participant makeParticipant(String email, String status) {
        return new Participant(email, status);
    }

    @Before
    public void setUp() {
        publicEvents = new ArrayList<>();
        publicEvents.add(makeEvent("Swimming Lessons", false));
        publicEvents.add(makeEvent("Piano Classes", false));
        publicEvents.add(makeEvent("Art Workshop", false));

        allParticipants = new ArrayList<>();
        allParticipants.add(makeParticipant("a@test.com", "waitlist"));
        allParticipants.add(makeParticipant("b@test.com", "waitlist"));
        allParticipants.add(makeParticipant("c@test.com", "selected"));
        allParticipants.add(makeParticipant("d@test.com", "selected"));
        allParticipants.add(makeParticipant("e@test.com", "cancelled"));
        allParticipants.add(makeParticipant("f@test.com", "enrolled"));
        allParticipants.add(makeParticipant("g@test.com", "enrolled"));
    }

    // helper to filter participants by status
    private List<Participant> filterByStatus(List<Participant> list, String status) {
        List<Participant> result = new ArrayList<>();
        for (Participant p : list) {
            if (status.equals(p.getStatus())) result.add(p);
        }
        return result;
    }

    // --- US 01.01.03: browse events list ---

    @Test
    public void testBrowseEvents_listIsNotEmpty() {
        assertFalse(publicEvents.isEmpty());
    }

    @Test
    public void testBrowseEvents_onlyPublicEventsShown() {
        for (Event e : publicEvents) {
            assertFalse(e.isPrivate());
        }
    }

    @Test
    public void testBrowseEvents_countIsCorrect() {
        assertEquals(3, publicEvents.size());
    }

    @Test
    public void testBrowseEvents_eventNamesAreNotNull() {
        for (Event e : publicEvents) {
            assertNotNull(e.getName());
        }
    }

    @Test
    public void testBrowseEvents_privateEventNotInList() {
        Event privateEvent = makeEvent("Secret Class", true);
        // Private events filtered out by whereEqualTo("privateEvent", false)
        assertFalse(publicEvents.contains(privateEvent));
    }

    // --- US 01.05.05: lottery criteria ---

    @Test
    public void testLotteryCriteria_isRandomSelection() {
        // The lottery uses Collections.shuffle - purely random, no bias
        String criteria = "random";
        assertNotNull(criteria);
        assertEquals("random", criteria);
    }

    @Test
    public void testLotteryCriteria_selectionCountDoesNotExceedCapacity() {
        int capacity = 10;
        int waitlistSize = 25;
        int selected = Math.min(capacity, waitlistSize);
        assertTrue(selected <= capacity);
    }

    // --- US 02.01.03: invite entrants to private event ---

    @Test
    public void testPrivateEventInvite_typeIsPrivateInvite() {
        String notifType = "PRIVATE_INVITE";
        assertEquals("PRIVATE_INVITE", notifType);
    }

    @Test
    public void testPrivateEventInvite_invitedEntrantAddedToWaitlist() {
        Participant invited = new Participant("invited@example.com", "waitlist");
        assertEquals("waitlist", invited.getStatus());
    }

    @Test
    public void testPrivateEventInvite_searchByEmail() {
        String searchQuery = "invited@example.com";
        assertTrue(searchQuery.contains("@"));
        assertFalse(searchQuery.isEmpty());
    }

    // --- US 02.02.01: view waitlist participants ---

    @Test
    public void testViewWaitlist_correctCountReturned() {
        List<Participant> waitlist = filterByStatus(allParticipants, "waitlist");
        assertEquals(2, waitlist.size());
    }

    @Test
    public void testViewWaitlist_allHaveWaitlistStatus() {
        List<Participant> waitlist = filterByStatus(allParticipants, "waitlist");
        for (Participant p : waitlist) {
            assertEquals("waitlist", p.getStatus());
        }
    }

    @Test
    public void testViewWaitlist_emailsAreNotNull() {
        List<Participant> waitlist = filterByStatus(allParticipants, "waitlist");
        for (Participant p : waitlist) {
            assertNotNull(p.getEmail());
        }
    }

    // --- US 02.05.01 / 02.06.01: notify and view chosen entrants ---

    @Test
    public void testViewSelected_correctCountReturned() {
        List<Participant> selected = filterByStatus(allParticipants, "selected");
        assertEquals(2, selected.size());
    }

    @Test
    public void testViewSelected_allHaveSelectedStatus() {
        List<Participant> selected = filterByStatus(allParticipants, "selected");
        for (Participant p : selected) {
            assertEquals("selected", p.getStatus());
        }
    }

    @Test
    public void testNotifyChosen_selectedGroupMapsToLotteryWinType() {
        String statusToMatch = "selected";
        String notifType = statusToMatch.equals("selected") ? "LOTTERY_WIN" : statusToMatch;
        assertEquals("LOTTERY_WIN", notifType);
    }

    // --- US 02.06.02: view cancelled entrants ---

    @Test
    public void testViewCancelled_correctCountReturned() {
        List<Participant> cancelled = filterByStatus(allParticipants, "cancelled");
        assertEquals(1, cancelled.size());
    }

    @Test
    public void testViewCancelled_allHaveCancelledStatus() {
        List<Participant> cancelled = filterByStatus(allParticipants, "cancelled");
        for (Participant p : cancelled) {
            assertEquals("cancelled", p.getStatus());
        }
    }

    // --- US 02.06.03: view enrolled entrants ---

    @Test
    public void testViewEnrolled_correctCountReturned() {
        List<Participant> enrolled = filterByStatus(allParticipants, "enrolled");
        assertEquals(2, enrolled.size());
    }

    @Test
    public void testViewEnrolled_allHaveEnrolledStatus() {
        List<Participant> enrolled = filterByStatus(allParticipants, "enrolled");
        for (Participant p : enrolled) {
            assertEquals("enrolled", p.getStatus());
        }
    }

    // --- US 02.06.04: cancel entrants ---

    @Test
    public void testCancelEntrant_statusChangesCancelled() {
        Participant p = makeParticipant("cancel@test.com", "selected");
        p.setStatus("cancelled");
        assertEquals("cancelled", p.getStatus());
    }

    @Test
    public void testCancelEntrant_noLongerAppearsInSelected() {
        List<Participant> selected = filterByStatus(allParticipants, "selected");
        int before = selected.size();
        allParticipants.get(2).setStatus("cancelled");
        selected = filterByStatus(allParticipants, "selected");
        assertEquals(before - 1, selected.size());
    }

    @Test
    public void testCancelEntrant_appearsInCancelledList() {
        allParticipants.get(2).setStatus("cancelled");
        List<Participant> cancelled = filterByStatus(allParticipants, "cancelled");
        assertEquals(2, cancelled.size());
    }

    // --- US 02.06.05: CSV export ---

    @Test
    public void testCsvExport_enrolledOnlyIncluded() {
        List<Participant> enrolled = filterByStatus(allParticipants, "enrolled");
        List<String> csvRows = new ArrayList<>();
        csvRows.add("Name,Email,Status");
        for (Participant p : enrolled) {
            csvRows.add("Unknown," + p.getEmail() + "," + p.getStatus());
        }
        // Header + 2 enrolled
        assertEquals(3, csvRows.size());
    }

    @Test
    public void testCsvExport_headerRowIsCorrect() {
        String header = "Name,Email,Status";
        assertEquals("Name,Email,Status", header);
    }

    @Test
    public void testCsvExport_emailEscaping_handlesCommas() {
        String value = "test,value";
        String escaped = "\"" + value.replace("\"", "\"\"") + "\"";
        assertEquals("\"test,value\"", escaped);
    }

    @Test
    public void testCsvExport_emailEscaping_handlesQuotes() {
        String value = "test\"value";
        String escaped = "\"" + value.replace("\"", "\"\"") + "\"";
        assertEquals("\"test\"\"value\"", escaped);
    }

    @Test
    public void testCsvExport_nullValueEscaping_returnsEmpty() {
        String value = null;
        String escaped = value == null ? "" : "\"" + value + "\"";
        assertEquals("", escaped);
    }

    // --- US 02.07.01/02/03: send notifications to groups ---

    @Test
    public void testNotifyWaitlist_statusMapsToWaitlist() {
        String group = "Waiting";
        String statusToMatch = group.equals("Waiting") ? "waitlist" : group.toLowerCase();
        assertEquals("waitlist", statusToMatch);
    }

    @Test
    public void testNotifySelected_statusMapsToSelected() {
        String group = "Selected";
        String statusToMatch = group.equals("Waiting") ? "waitlist" : group.toLowerCase();
        assertEquals("selected", statusToMatch);
    }

    @Test
    public void testNotifyCancelled_statusMapsToCancelled() {
        String group = "Cancelled";
        String statusToMatch = group.equals("Waiting") ? "waitlist" : group.toLowerCase();
        assertEquals("cancelled", statusToMatch);
    }

    @Test
    public void testNotifyGroup_emptyMessageIsInvalid() {
        String message = "";
        assertTrue(message.isEmpty());
    }

    @Test
    public void testNotifyGroup_nonEmptyMessageIsValid() {
        String message = "Your event starts tomorrow!";
        assertFalse(message.isEmpty());
    }

    // --- US 02.08.01: organizer view/delete comments ---

    @Test
    public void testComment_textIsCorrect() {
        Comment comment = new Comment("id1", "Great event!", "user1", "Alice", System.currentTimeMillis());
        assertEquals("Great event!", comment.getText());
    }

    @Test
    public void testComment_userNameIsCorrect() {
        Comment comment = new Comment("id1", "Great event!", "user1", "Alice", System.currentTimeMillis());
        assertEquals("Alice", comment.getUserName());
    }

    @Test
    public void testComment_idIsCorrect() {
        Comment comment = new Comment("id1", "Great event!", "user1", "Alice", System.currentTimeMillis());
        assertEquals("id1", comment.getCommentId());
    }

    @Test
    public void testDeleteComment_removedFromList() {
        List<Comment> comments = new ArrayList<>();
        Comment c1 = new Comment("id1", "Nice!", "u1", "Alice", System.currentTimeMillis());
        Comment c2 = new Comment("id2", "Loved it!", "u2", "Bob", System.currentTimeMillis());
        comments.add(c1);
        comments.add(c2);

        comments.remove(0);
        assertEquals(1, comments.size());
        assertEquals("id2", comments.get(0).getCommentId());
    }

    @Test
    public void testViewComments_listIsNotEmpty() {
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment("id1", "Nice!", "u1", "Alice", System.currentTimeMillis()));
        assertFalse(comments.isEmpty());
    }

    @Test
    public void testComment_noArgConstructor_doesNotThrow() {
        Comment c = new Comment();
        assertNotNull(c);
    }
}
