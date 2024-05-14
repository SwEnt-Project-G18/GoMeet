package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
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

class ProfileTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testProfile() {
    composeTestRule.setContent {
      Profile(
          NavigationActions(rememberNavController()),
          userId = "null",
          UserViewModel(UserRepository(Firebase.firestore)),
          EventViewModel("null", EventRepository(Firebase.firestore)))
    }

    composeTestRule.waitUntil(timeoutMillis = 10000) {
      composeTestRule.onNodeWithTag("Profile Picture").isDisplayed()
    }

    composeTestRule.onNodeWithText("My Profile").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Profile Picture").assertIsDisplayed()
    composeTestRule.onNodeWithText("Edit Profile").assertIsDisplayed()
    composeTestRule.onNodeWithText("Share Profile").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("EventsListHeader")[0].assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("EventsListHeader")[1].assertIsDisplayed()
  }
}
