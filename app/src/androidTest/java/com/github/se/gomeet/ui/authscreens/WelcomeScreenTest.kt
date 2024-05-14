package com.github.se.gomeet.ui.authscreens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WelcomeScreenTest {
  @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setup() {
    // Initialize Intents before each test
    Intents.init()
  }

  @After
  fun tearDown() {
    // Release Intents after each test
    Intents.release()
  }

  @Test
  fun googleSignInButtonShouldLaunchIntent() {

    rule.setContent { WelcomeScreen({}, {}, {}) }

    rule.onNodeWithContentDescription("GoMeet Logo").assertIsDisplayed()
    rule.onNodeWithText("Log In").assertExists().assertIsDisplayed()
    rule.onNodeWithText("Sign Up").assertExists().assertIsDisplayed()
    rule.onNodeWithContentDescription("Google logo").assertIsDisplayed().performClick()
    // Assert that an Intent to Google Mobile Services has been sent
    intended(toPackage("com.google.android.gms"))
  }
}
