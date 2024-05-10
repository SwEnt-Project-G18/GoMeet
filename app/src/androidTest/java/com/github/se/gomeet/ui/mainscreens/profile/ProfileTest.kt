package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
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
  fun profileUiTest() {
    composeTestRule.setContent {
      Profile(
          NavigationActions(rememberNavController()),
          userId = "1234",
          UserViewModel(UserRepository(Firebase.firestore)),
          EventViewModel("1234", EventRepository(Firebase.firestore)))
    }

    composeTestRule.onNodeWithText("My Profile").assertIsDisplayed()

    composeTestRule.onNodeWithContentDescription("image description").assertIsDisplayed()

    composeTestRule.onNodeWithText("Edit Profile").assertIsDisplayed()

    composeTestRule.onNodeWithText("Share Profile").assertIsDisplayed()
  }
}
