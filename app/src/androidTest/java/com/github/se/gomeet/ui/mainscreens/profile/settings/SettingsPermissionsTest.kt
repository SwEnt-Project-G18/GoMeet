package com.github.se.gomeet.ui.mainscreens.profile.settings

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsPermissionsTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun testSettingsPermissions() {
    composeTestRule.setContent { SettingsPermissions(NavigationActions(rememberNavController())) }

    composeTestRule.waitForIdle()

    // Test that the ui is correctly displayed
    composeTestRule.onNodeWithText("Open permissions").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onNodeWithText("Location").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onNodeWithText("Internet").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onNodeWithText("Notifications").assertIsDisplayed().assertHasClickAction()
  }
}
