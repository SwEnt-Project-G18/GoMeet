package com.github.se.gomeet.ui.mainscreens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun testTrends() {
    val uid = "TrendsTestUser"

    composeTestRule.setContent {
      Trends(
          currentUserId = uid,
          nav = NavigationActions(rememberNavController()),
          userViewModel = UserViewModel(),
          eventViewModel = EventViewModel(uid))
    }

    // Wait for the page to load
    composeTestRule.waitUntil(timeoutMillis = 10000) {
      composeTestRule.onNodeWithText("Sort").isDisplayed()
    }

    // Verify that the ui is correctly displayed
    composeTestRule.onNodeWithText("Sort").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText("Popularity").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText("Sort").performClick()
    composeTestRule.onNodeWithText("Name").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText("Sort").performClick()
    composeTestRule.onNodeWithText("Date").assertIsDisplayed().performClick()
  }
}
