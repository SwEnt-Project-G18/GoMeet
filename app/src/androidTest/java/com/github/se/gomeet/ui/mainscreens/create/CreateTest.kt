package com.github.se.gomeet.ui.mainscreens.create

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test

class CreateTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun testCreate() {
    lateinit var navController: NavHostController

    composeTestRule.setContent {
      navController = rememberNavController()
      Create(NavigationActions(navController))
    }

    // Check that the text "Choose your audience" is displayed
    composeTestRule.onNodeWithText("Choose your audience").assertIsDisplayed()

    // Check that the "Public" and "Private" buttons are displayed
    composeTestRule.onNodeWithText("Public").assertIsDisplayed()
    composeTestRule.onNodeWithText("Private").assertIsDisplayed()
  }
}
