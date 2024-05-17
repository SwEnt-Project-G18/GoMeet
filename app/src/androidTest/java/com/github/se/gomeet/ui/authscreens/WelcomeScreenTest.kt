package com.github.se.gomeet.ui.authscreens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsEnabled
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
    runBlocking {
      // Initialize Intents before each test
      Intents.init()
    }
  }

  @Test
  fun testWelcomeScreen() {
    composeTestRule.setContent { WelcomeScreen({}, {}) { _, _, _, _, _, _ -> } }

    composeTestRule.waitForIdle()

    // Test the ui and sign in with Google
    composeTestRule.onNodeWithContentDescription("GoMeet Logo").assertIsDisplayed()
    composeTestRule.onNodeWithText("Log In").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithText("Sign Up").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Google logo").assertIsDisplayed().performClick()

    // Assert that an Intent to Google Mobile Services has been sent
    intended(toPackage("com.google.android.gms"))
  }

  @After
  fun tearDown() {
    runBlocking {
      // Release Intents after each test
      Intents.release()
    }
  }
}
