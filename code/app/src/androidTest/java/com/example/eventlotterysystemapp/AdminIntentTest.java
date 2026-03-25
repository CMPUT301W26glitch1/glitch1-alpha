package com.example.eventlotterysystemapp;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.eventlotterysystemapp.ui.AdminDashboardActivity;
import com.example.eventlotterysystemapp.ui.AdminEventDetailActivity;
import com.example.eventlotterysystemapp.ui.AdminManageEventsActivity;
import com.example.eventlotterysystemapp.ui.AdminManageProfilesActivity;
import com.example.eventlotterysystemapp.ui.AdminProfileDetailActivity;
import com.example.eventlotterysystemapp.ui.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Intent tests for admin UI screens.
 */
@RunWith(AndroidJUnit4.class)
public class AdminIntentTest {

    // ================================================================
    // Login Screen Tests
    // ================================================================

    @Rule
    public ActivityScenarioRule<LoginActivity> loginRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testLoginScreenDisplayed() {
        // verify the login screen shows the email and password fields
        onView(withId(R.id.etEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.etPassword)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginContinueButtonDisplayed() {
        onView(withId(R.id.btnContinue)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginSignUpButtonDisplayed() {
        onView(withId(R.id.btnSignUp)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginEmptyFieldsShowsError() {
        // clicking continue with empty fields should show a toast
        // verify the button is clickable and the fields are still visible after
        onView(withId(R.id.btnContinue)).perform(click());
        onView(withId(R.id.etEmail)).check(matches(isDisplayed()));
    }

    // ================================================================
    // Admin Dashboard Tests
    // ================================================================

    @Test
    public void testAdminDashboardDisplayed() {
        try (ActivityScenario<AdminDashboardActivity> scenario =
                     ActivityScenario.launch(AdminDashboardActivity.class)) {
            // verify all 5 menu cards are displayed
            onView(withId(R.id.cardManageEvents)).check(matches(isDisplayed()));
            onView(withId(R.id.cardManageProfiles)).check(matches(isDisplayed()));
            onView(withId(R.id.cardManageImages)).check(matches(isDisplayed()));
            onView(withId(R.id.cardManageOrganizers)).check(matches(isDisplayed()));
            onView(withId(R.id.cardNotificationLogs)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testAdminDashboardLogoutButtonDisplayed() {
        try (ActivityScenario<AdminDashboardActivity> scenario =
                     ActivityScenario.launch(AdminDashboardActivity.class)) {
            onView(withId(R.id.btnLogout)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testAdminDashboardManageEventsNavigates() {
        try (ActivityScenario<AdminDashboardActivity> scenario =
                     ActivityScenario.launch(AdminDashboardActivity.class)) {
            // clicking Manage Events should open AdminManageEventsActivity
            onView(withId(R.id.cardManageEvents)).perform(click());
            onView(withId(R.id.recyclerEvents)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testAdminDashboardManageProfilesNavigates() {
        try (ActivityScenario<AdminDashboardActivity> scenario =
                     ActivityScenario.launch(AdminDashboardActivity.class)) {
            // clicking Manage Profiles should open AdminManageProfilesActivity
            onView(withId(R.id.cardManageProfiles)).perform(click());
            onView(withId(R.id.recyclerProfiles)).check(matches(isDisplayed()));
        }
    }

    // ================================================================
    // Manage Events Screen Tests
    // ================================================================

    @Test
    public void testManageEventsScreenDisplayed() {
        try (ActivityScenario<AdminManageEventsActivity> scenario =
                     ActivityScenario.launch(AdminManageEventsActivity.class)) {
            onView(withId(R.id.recyclerEvents)).check(matches(isDisplayed()));
        }
    }

    // ================================================================
    // Event Detail Screen Tests
    // ================================================================

    @Test
    public void testEventDetailScreenDisplayed() {
        // launch AdminEventDetailActivity
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                AdminEventDetailActivity.class);
        intent.putExtra("eventId", "testEventId123");
        intent.putExtra("eventName", "Test Event");

        try (ActivityScenario<AdminEventDetailActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnDeleteEvent)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testEventDetailDeleteButtonDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                AdminEventDetailActivity.class);
        intent.putExtra("eventId", "testEventId123");

        try (ActivityScenario<AdminEventDetailActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnDeleteEvent)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testEventDetailDeleteButtonShowsDialog() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                AdminEventDetailActivity.class);
        intent.putExtra("eventId", "testEventId123");
        intent.putExtra("eventName", "Test Event");

        try (ActivityScenario<AdminEventDetailActivity> scenario =
                     ActivityScenario.launch(intent)) {
            // tapping Delete should show a confirmation dialog
            onView(withId(R.id.btnDeleteEvent)).perform(click());
            onView(withText("Delete")).check(matches(isDisplayed()));
            onView(withText("Cancel")).check(matches(isDisplayed()));
        }
    }

    // ================================================================
    // Manage Profiles Screen Tests
    // ================================================================

    @Test
    public void testManageProfilesScreenDisplayed() {
        try (ActivityScenario<AdminManageProfilesActivity> scenario =
                     ActivityScenario.launch(AdminManageProfilesActivity.class)) {
            onView(withId(R.id.recyclerProfiles)).check(matches(isDisplayed()));
        }
    }

    // ================================================================
    // Profile Detail Screen Tests
    // ================================================================

    @Test
    public void testProfileDetailScreenDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                AdminProfileDetailActivity.class);
        intent.putExtra("profileId", "testProfileId123");
        intent.putExtra("profileName", "Test User");
        intent.putExtra("profileEmail", "test@test.com");
        intent.putExtra("profileRole", "Entrant");
        intent.putExtra("profilePhone", "123-456-7890");

        try (ActivityScenario<AdminProfileDetailActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnDeleteProfile)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testProfileDetailDeleteButtonShowsDialog() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                AdminProfileDetailActivity.class);
        intent.putExtra("profileId", "testProfileId123");
        intent.putExtra("profileName", "Test User");
        intent.putExtra("profileEmail", "test@test.com");
        intent.putExtra("profileRole", "Entrant");

        try (ActivityScenario<AdminProfileDetailActivity> scenario =
                     ActivityScenario.launch(intent)) {
            // tapping Remove Profile should show a confirmation dialog
            onView(withId(R.id.btnDeleteProfile)).perform(click());
            onView(withText("Remove")).check(matches(isDisplayed()));
            onView(withText("Cancel")).check(matches(isDisplayed()));
        }
    }
}
