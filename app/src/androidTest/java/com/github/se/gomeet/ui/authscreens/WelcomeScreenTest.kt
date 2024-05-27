package com.github.se.gomeet.ui.authscreens

import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WelcomeScreenTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setup() =
    runBlocking {
      // Initialize Intents before each test
      Intents.init()
    }

  @Test
  fun testWelcomeScreen() {
    composeTestRule.setContent {
      val chatClientDisconnected = remember { mutableStateOf(true) }
      WelcomeScreen({}, {}, { _, _, _, _, _, _ -> }, chatClientDisconnected)
    }

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
  fun tearDown() =
    runBlocking {
      // Release Intents after each test
      Intents.release()

      return@runBlocking
    }
}
