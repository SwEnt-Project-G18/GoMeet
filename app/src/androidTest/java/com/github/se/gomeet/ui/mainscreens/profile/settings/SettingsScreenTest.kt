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
import com.github.se.gomeet.R
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.UserViewModel
import io.github.kakaocup.kakao.common.utilities.getResourceString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun testSettingsScreen() {
    composeTestRule.setContent {
      SettingsScreen(NavigationActions(rememberNavController()), UserViewModel()) {}
    }

    composeTestRule.waitForIdle()

    // Test that the ui is correctly displayed
    composeTestRule.onNodeWithText(getResourceString(R.string.settings)).assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed().assertHasClickAction()

    composeTestRule
        .onNodeWithText(getResourceString(R.string.settings_section1))
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.settings_account_privacy))
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.settings_close_friends))
        .assertIsDisplayed()
    composeTestRule.onNodeWithText(getResourceString(R.string.settings_blocked)).assertIsDisplayed()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.settings_messages))
        .assertIsDisplayed()

    composeTestRule
        .onNodeWithText(getResourceString(R.string.settings_section2))
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.settings_suggested_content))
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.settings_device_permissions))
        .assertIsDisplayed()
        .assertHasClickAction()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.settings_accessibility))
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.settings_language))
        .assertIsDisplayed()

    composeTestRule
        .onNodeWithText(getResourceString(R.string.settings_more_info))
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.settings_help))
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasClickAction()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.settings_about))
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasClickAction()

    composeTestRule
        .onNodeWithText(getResourceString(R.string.log_out_button))
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onAllNodesWithContentDescription("Navigate next icon")[0]
        .performScrollTo()
        .assertExists()
  }
}
