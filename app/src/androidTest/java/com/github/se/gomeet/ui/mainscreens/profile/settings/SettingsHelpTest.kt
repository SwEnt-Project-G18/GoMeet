package com.github.se.gomeet.ui.mainscreens.profile.settings

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test

class SettingsHelpTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun testSettingsHelp() {
    composeTestRule.setContent { SettingsHelp(NavigationActions(rememberNavController())) }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("SettingsHelp").assertIsDisplayed()
  }
}
