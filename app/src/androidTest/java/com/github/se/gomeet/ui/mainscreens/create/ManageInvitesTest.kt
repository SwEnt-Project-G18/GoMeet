package com.github.se.gomeet.ui.mainscreens.create

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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

class ManageInvitesTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun testManageInvites() {
    composeTestRule.setContent {
      ManageInvites(
          currentUser = "null",
          currentEvent = "null",
          nav = NavigationActions(rememberNavController()),
          userViewModel = UserViewModel(UserRepository(Firebase.firestore)),
          eventViewModel = EventViewModel("null", EventRepository(Firebase.firestore)))
    }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText("Manage Invites").assertIsDisplayed()
    composeTestRule.onNodeWithText("To Invite").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText("Pending").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText("Accepted").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText("Refused").assertIsDisplayed().performClick()
  }
}
