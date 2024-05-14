package com.github.se.gomeet.ui.mainscreens.events

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.model.repository.EventRepository
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.junit.Rule
import org.junit.Test

class EventsTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testEvents() {
    composeTestRule.setContent {
      Events(
          "null",
          NavigationActions(rememberNavController()),
          UserViewModel(UserRepository(Firebase.firestore)),
          EventViewModel("null", EventRepository(Firebase.firestore)))
    }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText("Search").assertIsDisplayed()
    composeTestRule.onNodeWithTag("JoinedButton").assertIsDisplayed().performClick().performClick()
    composeTestRule
        .onNodeWithTag("FavouritesButton")
        .assertIsDisplayed()
        .performClick()
        .performClick()
    composeTestRule
        .onNodeWithTag("MyEventsButton")
        .assertIsDisplayed()
        .performClick()
        .performClick()
    composeTestRule.onNodeWithTag("JoinedTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("FavouritesTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MyEventsTitle").assertIsDisplayed()
  }
}
