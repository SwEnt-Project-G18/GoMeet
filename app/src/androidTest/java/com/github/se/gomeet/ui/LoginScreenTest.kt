package com.github.se.gomeet.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.viewmodel.AuthViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

  @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

  @Test fun testLoginScreen() {
    val authViewModel = AuthViewModel()

    rule.setContent {
      val nav = rememberNavController()
      LoginScreen(authViewModel) {
        NavigationActions(nav).navigateTo(TOP_LEVEL_DESTINATIONS[1])
      }

      // Test the UI elements
      rule.onNodeWithText("Login").assertIsDisplayed()

      rule.onNodeWithText("Email").assertIsDisplayed()
      rule.onNodeWithText("Password").assertIsDisplayed()
      rule.onNodeWithText("Log in").assertIsNotEnabled().assertHasClickAction()

      // Enter email and password
      rule.onNodeWithText("Email").performTextInput("test@example.com")
      rule.onNodeWithText("Password").performTextInput("password")

      // Click on the "Log in" button
      rule.onNodeWithText("Log in").assertIsEnabled()
      rule.onNodeWithText("Log in").performClick()

      rule.waitForIdle()
      assert(nav.currentDestination?.route == Route.EXPLORE)

    }
  }
}
