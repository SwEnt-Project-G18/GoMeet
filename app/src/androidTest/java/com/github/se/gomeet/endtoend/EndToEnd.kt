package com.github.se.gomeet.endtoend

import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.se.gomeet.MainActivity
import com.github.se.gomeet.screens.CreateEventScreen
import com.github.se.gomeet.screens.CreateScreen
import com.github.se.gomeet.screens.ExploreScreen
import com.github.se.gomeet.screens.LoginScreen
import com.github.se.gomeet.screens.WelcomeScreenScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.junit4.MockKRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EndToEndTest : TestCase() {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @get:Rule val mockkRule = MockKRule(this)

  @get:Rule
  var permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

  @Test
  fun test() = run {
    ComposeScreen.onComposeScreen<WelcomeScreenScreen>(composeTestRule) {
      step("Click on log in button") {
        logInButton {
          assertIsDisplayed()
          performClick()
        }
      }
    }

    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
      step("Log in with email and password") {
        logInButton {
          assertIsDisplayed()
          assertIsNotEnabled()
        }
        emailField {
          assertIsDisplayed()
          performTextInput("qwe@asd.com")
        }
        passwordField {
          assertIsDisplayed()
          performTextInput("123456")
        }
        logInButton {
          assertIsEnabled()
          performClick()
        }
      }
    }

    // First ensure login and switch to the expected screen
    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
      step("Log in with email and password") { logInButton { performClick() } }
      composeTestRule.waitUntil(timeoutMillis = 100000) {
        composeTestRule.onNodeWithTag("CreateUI").isDisplayed()
      }
    }

    ComposeScreen.onComposeScreen<CreateScreen>(composeTestRule) {
      step("Select which type of event to create") {
        createPublicEventButton {
          assertIsDisplayed()
          performClick()
        }
      }
    }

    ComposeScreen.onComposeScreen<CreateEventScreen>(composeTestRule) {
      step("Create an event") {
        title {
          assertIsDisplayed()
          performTextInput("Title")
        }
        description {
          assertIsDisplayed()
          performTextInput("Description")
        }
        location {
          assertIsDisplayed()
          performTextInput("Lausanne")
        }
        dropDownMenu { assertIsDisplayed() }
        date {
          assertIsDisplayed()
          performTextInput("2024-07-23")
        }
        price {
          assertIsDisplayed()
          performTextInput("0.0")
        }
        link {
          assertIsDisplayed()
          performTextInput("https://example.com")
        }
        postButton {
          assertIsDisplayed()
          performClick()
        }
        switchToExplore { performClick() }
      }
    }

    ComposeScreen.onComposeScreen<ExploreScreen>(composeTestRule) {
      composeTestRule.waitUntil(timeoutMillis = 100000) {
        composeTestRule.onNodeWithTag("Map").isDisplayed()
      }

      step("Check that the map works") {
        map { assertIsDisplayed() }
        searchBar { assertIsDisplayed() }
        currentLocationButton {
          assertIsDisplayed()
          performClick()
        }
      }
    }
  }
}
