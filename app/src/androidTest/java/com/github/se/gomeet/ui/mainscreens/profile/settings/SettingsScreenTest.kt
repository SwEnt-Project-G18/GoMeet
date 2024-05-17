package com.github.se.gomeet.ui.mainscreens.profile.settings

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun testSettingsScreen() {
    composeTestRule.setContent { SettingsScreen(NavigationActions(rememberNavController())) }

    composeTestRule.waitForIdle()

    // Test that the ui is correctly displayed
    composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed().assertHasClickAction()

    composeTestRule.onNodeWithText("Who can see your content").assertIsDisplayed()
    composeTestRule.onNodeWithText("Account privacy").assertIsDisplayed()
    composeTestRule.onNodeWithText("Close friends").assertIsDisplayed()
    composeTestRule.onNodeWithText("Blocked").assertIsDisplayed()
    composeTestRule.onNodeWithText("Messages").assertIsDisplayed()

    composeTestRule.onNodeWithText("Your app and media").assertIsDisplayed()
    composeTestRule.onNodeWithText("Suggested content").assertIsDisplayed()
    composeTestRule.onNodeWithText("Device permissions").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onNodeWithText("Accessibility").assertIsDisplayed()
    composeTestRule.onNodeWithText("Language").assertIsDisplayed()

    composeTestRule.onNodeWithText("More info and support").performScrollTo().assertIsDisplayed()
    composeTestRule
        .onNodeWithText("Help")
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasClickAction()
    composeTestRule
        .onNodeWithText("About")
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasClickAction()

    composeTestRule.onNodeWithText("Log out").performScrollTo().assertIsDisplayed()
    composeTestRule
        .onAllNodesWithContentDescription("Navigate next icon")[0]
        .performScrollTo()
        .assertExists()
  }
}
