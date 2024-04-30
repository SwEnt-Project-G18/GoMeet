package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.ui.mainscreens.profile.Profile
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.UserViewModel
import org.junit.Rule
import org.junit.Test

class ProfileTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun profileUiTest() {
    composeTestRule.setContent {
      Profile(NavigationActions(rememberNavController()), UserViewModel())
    }

    composeTestRule.onNodeWithText("My Profile").assertIsDisplayed()

    composeTestRule.onNodeWithContentDescription("image description").assertIsDisplayed()

    composeTestRule.onNodeWithText("Edit Profile").performClick()

    composeTestRule.onNodeWithText("Share Profile").assertIsDisplayed()
  }
}
