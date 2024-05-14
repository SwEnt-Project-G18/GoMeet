package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test

class EditProfileTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testEditProfile() {
    composeTestRule.setContent { EditProfile(NavigationActions(rememberNavController())) }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithContentDescription("Profile Picture").assertIsDisplayed()
    composeTestRule.onNodeWithText("First Name").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("Last Name").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("Email Address").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("Username").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("Phone Number").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("Country").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("Edit Tags").performScrollTo().assertIsDisplayed()
  }
}
