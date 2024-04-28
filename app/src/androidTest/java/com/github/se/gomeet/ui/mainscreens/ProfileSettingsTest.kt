package com.github.se.gomeet.ui.mainscreens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.ui.mainscreens.profile.SettingsScreen
import com.github.se.gomeet.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileSettingsTest {

  @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun testProfileSettingsScreen() {
    lateinit var navController: NavHostController

    rule.setContent {
      navController = rememberNavController()
      SettingsScreen(NavigationActions(navController))
    }

    rule.onNodeWithText("Settings").assertIsDisplayed()
    rule.onNodeWithContentDescription("Back button").assertIsDisplayed()
    rule.onNodeWithText("Back").assertIsDisplayed()
    rule.onNodeWithText("Who can see your content").assertIsDisplayed()
    rule.onNodeWithText("Account privacy").assertIsDisplayed()
    rule.onNodeWithText("Close friends").assertIsDisplayed()
    rule.onNodeWithText("Blocked").assertIsDisplayed()
    rule.onNodeWithText("Messages").assertIsDisplayed()
    rule.onNodeWithText("Your app and media").assertIsDisplayed()
    rule.onNodeWithText("Suggested content").assertIsDisplayed()
    rule.onNodeWithText("Device permissions").assertIsDisplayed()
    rule.onNodeWithText("Accessibility").assertIsDisplayed()
    rule.onNodeWithText("Language").assertIsDisplayed()
    rule.onNodeWithText("More info and support").performScrollTo().assertIsDisplayed()
    rule.onNodeWithText("Help").performScrollTo().assertIsDisplayed()
    rule.onNodeWithText("About").performScrollTo().assertIsDisplayed()
    rule.onNodeWithText("Log out").performScrollTo().assertIsDisplayed()
    rule.onAllNodesWithContentDescription("Navigate next icon")[0].performScrollTo().assertExists()
  }
}
