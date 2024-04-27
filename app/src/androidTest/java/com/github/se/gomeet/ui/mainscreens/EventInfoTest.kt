package com.github.se.gomeet.ui.mainscreens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventInfoTest {
  @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun uiElementsDisplayed() {
    lateinit var navController: NavHostController

    rule.setContent {
      navController = rememberNavController()
      EventInfoScreen(navController)
    }

    rule.onNodeWithTag("TopBar").assertIsDisplayed()
    rule.onNodeWithTag("EventHeader").assertIsDisplayed()
    rule.onNodeWithTag("EventImage").assertIsDisplayed()
    rule.onNodeWithTag("EventDescription").assertExists()
    rule.onNodeWithTag("EventButton").assertIsDisplayed()
    rule.onNodeWithTag("MapView").assertIsDisplayed()
  }
}
