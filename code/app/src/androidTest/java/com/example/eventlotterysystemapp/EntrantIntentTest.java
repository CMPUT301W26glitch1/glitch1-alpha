package com.example.eventlotterysystemapp;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.eventlotterysystemapp.data.models.User;
import com.example.eventlotterysystemapp.data.models.UserSession;
import com.example.eventlotterysystemapp.ui.EntrantNotificationListActivity;
import com.example.eventlotterysystemapp.ui.EventListActivity;
import com.example.eventlotterysystemapp.ui.FilterActivity;
import com.example.eventlotterysystemapp.ui.LoginActivity;
import com.example.eventlotterysystemapp.ui.LotteryInfoActivity;
import com.example.eventlotterysystemapp.ui.ProfileActivity;
import com.example.eventlotterysystemapp.ui.RegistrationActivity;
import com.example.eventlotterysystemapp.ui.SettingsActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * Instrumented UI tests for entrant screens.
 * US 01.01.03 - browse events
 * US 01.01.05 - search events
 * US 01.01.04 / 01.01.06 - filter events
 * US 01.02.01 - registration screen
 * US 01.02.02 - profile/update screen
 * US 01.04.01/02 - notification list screen
 * US 01.04.03 - settings/opt-out screen
 * US 01.05.05 - lottery info screen
 */
@RunWith(AndroidJUnit4.class)
public class EntrantIntentTest {

    @Before
    public void setUp() {
        // Set a mock user session so screens that require it don't crash
        UserSession.setUser(new User("Test Entrant", "hash", "entrant@test.com", "Entrant", "device1"));
    }

    // ================================================================
    // US 01.02.01 - Registration Screen
    // ================================================================

    @Test
    public void testRegistrationScreen_isDisplayed() {
        try (ActivityScenario<RegistrationActivity> scenario =
                     ActivityScenario.launch(RegistrationActivity.class)) {
            onView(withId(R.id.username)).check(matches(isDisplayed()));
            onView(withId(R.id.email)).check(matches(isDisplayed()));
            onView(withId(R.id.password)).check(matches(isDisplayed()));
            onView(withId(R.id.roles)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testRegistrationScreen_registerButtonIsDisplayed() {
        try (ActivityScenario<RegistrationActivity> scenario =
                     ActivityScenario.launch(RegistrationActivity.class)) {
            onView(withId(R.id.registerButton)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testRegistrationScreen_returnButtonIsDisplayed() {
        try (ActivityScenario<RegistrationActivity> scenario =
                     ActivityScenario.launch(RegistrationActivity.class)) {
            onView(withId(R.id.returnButton)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testRegistrationScreen_phoneFieldIsDisplayed() {
        try (ActivityScenario<RegistrationActivity> scenario =
                     ActivityScenario.launch(RegistrationActivity.class)) {
            onView(withId(R.id.username2)).check(matches(isDisplayed()));
        }
    }

    // ================================================================
    // US 01.01.03 - Browse Events (EventListActivity)
    // ================================================================

    @Test
    public void testEventListScreen_recyclerViewIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                EventListActivity.class);
        intent.putExtra("USER_EMAIL", "entrant@test.com");
        try (ActivityScenario<EventListActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventRecyclerView)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testEventListScreen_notificationsFabIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                EventListActivity.class);
        intent.putExtra("USER_EMAIL", "entrant@test.com");
        try (ActivityScenario<EventListActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.fabNotifications)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testEventListScreen_cameraFabIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                EventListActivity.class);
        intent.putExtra("USER_EMAIL", "entrant@test.com");
        try (ActivityScenario<EventListActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.fabCamera)).check(matches(isDisplayed()));
        }
    }

    // ================================================================
    // US 01.01.05 - Search Events
    // ================================================================

    @Test
    public void testEventListScreen_searchBarIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                EventListActivity.class);
        intent.putExtra("USER_EMAIL", "entrant@test.com");
        try (ActivityScenario<EventListActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.searchEvents)).check(matches(isDisplayed()));
        }
    }

    // ================================================================
    // US 01.01.04 / 01.01.06 - Filter Events
    // ================================================================

    @Test
    public void testEventListScreen_filterButtonIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                EventListActivity.class);
        intent.putExtra("USER_EMAIL", "entrant@test.com");
        try (ActivityScenario<EventListActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnFilter)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testFilterScreen_availabilitySpinnerIsDisplayed() {
        try (ActivityScenario<FilterActivity> scenario =
                     ActivityScenario.launch(FilterActivity.class)) {
            onView(withId(R.id.spinnerAvailability)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testFilterScreen_interestsSpinnerIsDisplayed() {
        try (ActivityScenario<FilterActivity> scenario =
                     ActivityScenario.launch(FilterActivity.class)) {
            onView(withId(R.id.spinnerInterests)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testFilterScreen_applyButtonIsDisplayed() {
        try (ActivityScenario<FilterActivity> scenario =
                     ActivityScenario.launch(FilterActivity.class)) {
            onView(withId(R.id.btnApplyFilter)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testFilterScreen_resetButtonIsDisplayed() {
        try (ActivityScenario<FilterActivity> scenario =
                     ActivityScenario.launch(FilterActivity.class)) {
            onView(withId(R.id.btnReset)).check(matches(isDisplayed()));
        }
    }

    // ================================================================
    // US 01.02.02 - Profile / Update Screen
    // ================================================================

    @Test
    public void testProfileScreen_nameFieldIsDisplayed() {
        try (ActivityScenario<ProfileActivity> scenario =
                     ActivityScenario.launch(ProfileActivity.class)) {
            onView(withId(R.id.etName)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testProfileScreen_emailFieldIsDisplayed() {
        try (ActivityScenario<ProfileActivity> scenario =
                     ActivityScenario.launch(ProfileActivity.class)) {
            onView(withId(R.id.etEmail)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testProfileScreen_phoneFieldIsDisplayed() {
        try (ActivityScenario<ProfileActivity> scenario =
                     ActivityScenario.launch(ProfileActivity.class)) {
            onView(withId(R.id.etPhone)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testProfileScreen_saveButtonIsDisplayed() {
        try (ActivityScenario<ProfileActivity> scenario =
                     ActivityScenario.launch(ProfileActivity.class)) {
            onView(withId(R.id.btnSave)).check(matches(isDisplayed()));
        }
    }

    // ================================================================
    // US 01.04.01 / 01.04.02 - Notification List Screen
    // ================================================================

    @Test
    public void testNotificationListScreen_recyclerViewIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                EntrantNotificationListActivity.class);
        intent.putExtra("USER_EMAIL", "entrant@test.com");
        try (ActivityScenario<EntrantNotificationListActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.rvNotifications)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testNotificationListScreen_backButtonIsDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                EntrantNotificationListActivity.class);
        intent.putExtra("USER_EMAIL", "entrant@test.com");
        try (ActivityScenario<EntrantNotificationListActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnBack)).check(matches(isDisplayed()));
        }
    }

    // ================================================================
    // US 01.04.03 - Settings / Opt-Out Screen
    // ================================================================

    @Test
    public void testSettingsScreen_optOutSwitchIsDisplayed() {
        try (ActivityScenario<SettingsActivity> scenario =
                     ActivityScenario.launch(SettingsActivity.class)) {
            onView(withId(R.id.switchOptOut)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testSettingsScreen_accessibilitySwitchIsDisplayed() {
        try (ActivityScenario<SettingsActivity> scenario =
                     ActivityScenario.launch(SettingsActivity.class)) {
            onView(withId(R.id.switchAccessibility)).check(matches(isDisplayed()));
        }
    }

    // ================================================================
    // US 01.05.05 - Lottery Info Screen
    // ================================================================

    @Test
    public void testLotteryInfoScreen_backButtonIsDisplayed() {
        try (ActivityScenario<LotteryInfoActivity> scenario =
                     ActivityScenario.launch(LotteryInfoActivity.class)) {
            onView(withId(R.id.btnBack)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testLotteryInfoScreen_backButtonClosesScreen() {
        try (ActivityScenario<LotteryInfoActivity> scenario =
                     ActivityScenario.launch(LotteryInfoActivity.class)) {
            onView(withId(R.id.btnBack)).perform(click());
        }
    }
}
