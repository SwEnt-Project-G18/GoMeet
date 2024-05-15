package com.github.se.gomeet.ui.mainscreens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.model.repository.EventRepository
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TrendsTest {

  @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun uiElementsDisplayed() {
    lateinit var navController: NavHostController

    rule.setContent {
      navController = rememberNavController()
      Trends(
          currentUser = "NEEGn5cbkJZDXaezeGdfd2D4u6b2",
          nav = NavigationActions(rememberNavController()),
          userViewModel = UserViewModel(UserRepository(Firebase.firestore)),
          eventViewModel =
              EventViewModel("NEEGn5cbkJZDXaezeGdfd2D4u6b2", EventRepository(Firebase.firestore)))
    }

    rule.onNodeWithText("Sort").assertIsDisplayed().performClick()
    rule.onNodeWithText("Popularity").assertIsDisplayed().performClick()
    rule.onNodeWithText("Sort").performClick()
    rule.onNodeWithText("Name").assertIsDisplayed().performClick()
    rule.onNodeWithText("Sort").performClick()
    rule.onNodeWithText("Date").assertIsDisplayed().performClick()
  }
}
