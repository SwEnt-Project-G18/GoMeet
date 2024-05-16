package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test

class NotificationsTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testNotifications() {
    // Test the Notifications screen
    composeTestRule.setContent { Notifications(NavigationActions(rememberNavController()), "1234") }

    composeTestRule.onNodeWithTag("NotificationsScreen").assertExists()
    composeTestRule.onNodeWithText("Invitations").assertIsDisplayed()
    composeTestRule.onNodeWithText("Messages").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Go Back").assertIsDisplayed()
    //    composeTestRule.onNodeWithTag("InviterUserName").assertIsDisplayed()
    //    composeTestRule.onNodeWithTag("EventCard").assertIsDisplayed()
    //    composeTestRule.onNodeWithTag("EventDate").assertIsDisplayed()
    //    composeTestRule.onNodeWithTag("AcceptButton").assertIsDisplayed().assertIsEnabled()
    //    composeTestRule.onNodeWithTag("RejectButton").assertIsDisplayed().assertIsEnabled()
    //    composeTestRule.onNodeWithTag("EventImage").assertIsDisplayed()
  }
}
