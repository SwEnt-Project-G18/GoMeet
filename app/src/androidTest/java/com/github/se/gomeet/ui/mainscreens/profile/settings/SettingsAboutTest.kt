package com.github.se.gomeet.ui.mainscreens.profile.settings

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsAboutTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun testSettingsAbout() {
    composeTestRule.setContent { SettingsAbout(NavigationActions(rememberNavController())) }

    composeTestRule.waitForIdle()

    // Test that the ui is correctly displayed
    composeTestRule.onNodeWithTag("SettingsAbout").assertIsDisplayed()
  }
}
