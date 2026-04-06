package com.example.eventlotterysystemapp;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.eventlotterysystemapp.ui.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

/**
 * Simple UI tests for LoginActivity.
 * No Intents, no extra dependencies.
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityIntentTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testLoginScreenUIElements() {
        onView(withId(R.id.etEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.etPassword)).check(matches(isDisplayed()));
        onView(withId(R.id.btnContinue)).check(matches(isDisplayed()));
        onView(withId(R.id.btnSignUp)).check(matches(isDisplayed()));
    }

    @Test
    public void testEmptyLoginFields() {
        // Click Continue with empty fields
        onView(withId(R.id.btnContinue)).perform(click());

        // Fields should still be visible (user stays on login)
        onView(withId(R.id.etEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.etPassword)).check(matches(isDisplayed()));
    }

    @Test
    public void testEnterTextInFields() {
        onView(withId(R.id.etEmail)).perform(typeText("user@test.com"), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText("password123"), closeSoftKeyboard());

        onView(withId(R.id.etEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.etPassword)).check(matches(isDisplayed()));
    }

    @Test
    public void testClickButtons() {
        onView(withId(R.id.btnSignUp)).perform(click());
        onView(withId(R.id.btnContinue)).perform(click());
    }
}