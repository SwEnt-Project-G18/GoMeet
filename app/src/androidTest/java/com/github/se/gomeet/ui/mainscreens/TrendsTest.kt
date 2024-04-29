package com.github.se.gomeet.ui.mainscreens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
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
          userViewModel = UserViewModel(),
          eventViewModel = EventViewModel())
    }

    rule.onAllNodesWithText("Trends").apply {
      fetchSemanticsNodes().forEachIndexed { i, _ -> get(i).assertIsDisplayed() }
    }
  }
}
