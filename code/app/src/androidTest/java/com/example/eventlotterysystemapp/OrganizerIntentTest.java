package com.example.eventlotterysystemapp;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.eventlotterysystemapp.data.models.User;
import com.example.eventlotterysystemapp.data.models.UserSession;
import com.example.eventlotterysystemapp.ui.organizer.CreateEventActivity;
import com.example.eventlotterysystemapp.ui.organizer.EventParticipantsActivity;
import com.example.eventlotterysystemapp.ui.organizer.NotificationActivity;
import com.example.eventlotterysystemapp.ui.organizer.OrganizerMainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Instrumented UI tests for organizer screens.
 * US 02.01.01 - create public event screen
 * US 02.01.02 - private event toggle
 * US 02.01.04 - registration period fields
 * US 02.02.01 - view waitlist (participants screen)
 * US 02.02.03 - geolocation toggle
 * US 02.03.01 - waitlist limit field
 * US 02.04.01 - poster upload
 * US 02.05.01/02/03 - notify/lottery/replacement buttons
 * US 02.06.01-04 - participant tabs
 * US 02.06.05 - CSV export button
 * US 02.07.01/02/03 - notification screen
 * US 02.09.01 - co-organizer invite button
 */
@RunWith(AndroidJUnit4.class)
public class OrganizerIntentTest {

    @Before
    public void setUp() {
        UserSession.setUser(new User("Test Organizer", "hash", "org@test.com", "Organizer", "device1"));
    }

    // ================================================================
    // Organizer Main Screen
    // ================================================================

    @Test
    public void testOrganizerMainScreen_recyclerViewIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                OrganizerMainActivity.class);
        intent.putExtra("USER_EMAIL", "org@test.com");
        try (ActivityScenario<OrganizerMainActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testOrganizerMainScreen_createEventButtonIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                OrganizerMainActivity.class);
        intent.putExtra("USER_EMAIL", "org@test.com");
        try (ActivityScenario<OrganizerMainActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnCreateEvent)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testOrganizerMainScreen_logoutButtonIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                OrganizerMainActivity.class);
        intent.putExtra("USER_EMAIL", "org@test.com");
        try (ActivityScenario<OrganizerMainActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnLogout)).check(matches(isDisplayed()));
        }
    }

    // ================================================================
    // US 02.01.01/02/03/04 - Create Event Screen
    // ================================================================

    @Test
    public void testCreateEventScreen_titleFieldIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                CreateEventActivity.class);
        intent.putExtra("USER_EMAIL", "org@test.com");
        try (ActivityScenario<CreateEventActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventTitle)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testCreateEventScreen_descriptionFieldIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                CreateEventActivity.class);
        intent.putExtra("USER_EMAIL", "org@test.com");
        try (ActivityScenario<CreateEventActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventDescription)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testCreateEventScreen_categoryFieldIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                CreateEventActivity.class);
        intent.putExtra("USER_EMAIL", "org@test.com");
        try (ActivityScenario<CreateEventActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventCategory)).check(matches(isDisplayed()));
        }
    }

    // US 02.01.04 - registration period fields
    @Test
    public void testCreateEventScreen_regStartFieldIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                CreateEventActivity.class);
        intent.putExtra("USER_EMAIL", "org@test.com");
        try (ActivityScenario<CreateEventActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.registrationStart)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testCreateEventScreen_regEndFieldIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                CreateEventActivity.class);
        intent.putExtra("USER_EMAIL", "org@test.com");
        try (ActivityScenario<CreateEventActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.registrationEnd)).check(matches(isDisplayed()));
        }
    }

    // US 02.02.03 - geolocation toggle
    @Test
    public void testCreateEventScreen_geoLocationSwitchIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                CreateEventActivity.class);
        intent.putExtra("USER_EMAIL", "org@test.com");
        try (ActivityScenario<CreateEventActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.geoLocation)).check(matches(isDisplayed()));
        }
    }

    // US 02.01.02 - private event switch
    @Test
    public void testCreateEventScreen_privateEventSwitchIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                CreateEventActivity.class);
        intent.putExtra("USER_EMAIL", "org@test.com");
        try (ActivityScenario<CreateEventActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.privateEventSwitch)).check(matches(isDisplayed()));
        }
    }

    // US 02.03.01 - waitlist limit
    @Test
    public void testCreateEventScreen_listLimitFieldIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                CreateEventActivity.class);
        intent.putExtra("USER_EMAIL", "org@test.com");
        try (ActivityScenario<CreateEventActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.listLimit)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testCreateEventScreen_maxParticipantsFieldIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                CreateEventActivity.class);
        intent.putExtra("USER_EMAIL", "org@test.com");
        try (ActivityScenario<CreateEventActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.maxParticipants)).check(matches(isDisplayed()));
        }
    }

    // US 02.04.01 - poster upload
    @Test
    public void testCreateEventScreen_selectImageButtonIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                CreateEventActivity.class);
        intent.putExtra("USER_EMAIL", "org@test.com");
        try (ActivityScenario<CreateEventActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.selectImageBtn)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testCreateEventScreen_nextButtonIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                CreateEventActivity.class);
        intent.putExtra("USER_EMAIL", "org@test.com");
        try (ActivityScenario<CreateEventActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.nextBtn)).check(matches(isDisplayed()));
        }
    }

    // ================================================================
    // US 02.02.01 / 02.06.01-04 - Participants Screen (tabs)
    // ================================================================

    @Test
    public void testParticipantsScreen_tabLayoutIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                EventParticipantsActivity.class);
        intent.putExtra("EVENT_ID", "testEvent123");
        try (ActivityScenario<EventParticipantsActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.tabLayout)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testParticipantsScreen_waitlistTabIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                EventParticipantsActivity.class);
        intent.putExtra("EVENT_ID", "testEvent123");
        try (ActivityScenario<EventParticipantsActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withText("Waitlist")).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testParticipantsScreen_selectedTabIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                EventParticipantsActivity.class);
        intent.putExtra("EVENT_ID", "testEvent123");
        try (ActivityScenario<EventParticipantsActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withText("Selected")).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testParticipantsScreen_cancelledTabIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                EventParticipantsActivity.class);
        intent.putExtra("EVENT_ID", "testEvent123");
        try (ActivityScenario<EventParticipantsActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withText("Cancelled")).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testParticipantsScreen_enrolledTabIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                EventParticipantsActivity.class);
        intent.putExtra("EVENT_ID", "testEvent123");
        try (ActivityScenario<EventParticipantsActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withText("Enrolled")).check(matches(isDisplayed()));
        }
    }

    // US 02.09.01 - co-organizer button
    @Test
    public void testParticipantsScreen_inviteCoorgButtonIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                EventParticipantsActivity.class);
        intent.putExtra("EVENT_ID", "testEvent123");
        try (ActivityScenario<EventParticipantsActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnInviteCoorg)).check(matches(isDisplayed()));
        }
    }

    // US 02.06.05 - CSV export
    @Test
    public void testParticipantsScreen_exportCsvButtonIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                EventParticipantsActivity.class);
        intent.putExtra("EVENT_ID", "testEvent123");
        try (ActivityScenario<EventParticipantsActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnExportCsv)).check(matches(isDisplayed()));
        }
    }

    // US 02.07.01/02/03 - notify button
    @Test
    public void testParticipantsScreen_notifyButtonIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                EventParticipantsActivity.class);
        intent.putExtra("EVENT_ID", "testEvent123");
        try (ActivityScenario<EventParticipantsActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnNotifyTop)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testParticipantsScreen_returnButtonIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                EventParticipantsActivity.class);
        intent.putExtra("EVENT_ID", "testEvent123");
        try (ActivityScenario<EventParticipantsActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnReturn)).check(matches(isDisplayed()));
        }
    }

    // ================================================================
    // US 02.07.01/02/03 - Notification Activity
    // ================================================================

    @Test
    public void testNotificationScreen_spinnerIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                NotificationActivity.class);
        intent.putExtra("EVENT_ID", "testEvent123");
        try (ActivityScenario<NotificationActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.spinnerSendTo)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testNotificationScreen_messageFieldIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                NotificationActivity.class);
        intent.putExtra("EVENT_ID", "testEvent123");
        try (ActivityScenario<NotificationActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.notificationMessage)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testNotificationScreen_sendButtonIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                NotificationActivity.class);
        intent.putExtra("EVENT_ID", "testEvent123");
        try (ActivityScenario<NotificationActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.sendNotificationBtn)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testNotificationScreen_backButtonIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                NotificationActivity.class);
        intent.putExtra("EVENT_ID", "testEvent123");
        try (ActivityScenario<NotificationActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.BtnBack)).check(matches(isDisplayed()));
        }
    }
}
