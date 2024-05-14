package com.github.se.gomeet.ui.mainscreens.profile

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
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

class OthersProfileTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun testOthersProfile() {

    composeTestRule.setContent {
      OthersProfile(
          NavigationActions(rememberNavController()),
          "other_null",
          UserViewModel(UserRepository(Firebase.firestore)),
          EventViewModel("null", EventRepository(Firebase.firestore)))
    }

    composeTestRule.onNodeWithTag("TopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("UserInfo").assertIsDisplayed()
    composeTestRule.onNodeWithText("Follow").assertIsDisplayed()
    composeTestRule.onNodeWithText("Message").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("EventsListHeader")[0].assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("EventsListHeader")[1].assertIsDisplayed()
  }
}
