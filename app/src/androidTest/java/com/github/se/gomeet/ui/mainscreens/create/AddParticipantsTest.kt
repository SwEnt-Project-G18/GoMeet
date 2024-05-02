package com.github.se.gomeet.ui.mainscreens.create

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test

class AddParticipantsTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun profileUiTest() {
    composeTestRule.setContent { AddParticipants(NavigationActions(rememberNavController())) }

    composeTestRule.onNodeWithTag("AddParticipantsScreen").assertIsDisplayed()
  }
}
