package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test

class EditProfileTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun uiElementsDisplayed() {
    composeTestRule.setContent { EditProfile(NavigationActions(rememberNavController())) }

    composeTestRule.onNodeWithText("My Profile").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("image description").assertIsDisplayed()
    composeTestRule.onNodeWithText("First Name").assertIsDisplayed()
    composeTestRule.onNodeWithText("Last Name").assertIsDisplayed()
    composeTestRule.onNodeWithText("Email Address").assertIsDisplayed()
    composeTestRule.onNodeWithText("Username").assertIsDisplayed()
    composeTestRule.onNodeWithText("Phone Number").assertIsDisplayed()
    composeTestRule.onNodeWithText("Country").assertIsDisplayed()
    composeTestRule.onNodeWithText("Edit Tags").assertIsDisplayed()
  }
}
