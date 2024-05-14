package com.github.se.gomeet.ui.mainscreens.profile

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.junit.Rule
import org.junit.Test

class FollowingFollowersTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun testFollowingFollowers() {
    composeTestRule.setContent {
      FollowingFollowers(
          NavigationActions(rememberNavController()),
          "null",
          UserViewModel(UserRepository(Firebase.firestore)),
          true)
    }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText("Following").assertIsDisplayed()
    composeTestRule.onNodeWithText("Followers").assertIsDisplayed().performClick()
  }
}
