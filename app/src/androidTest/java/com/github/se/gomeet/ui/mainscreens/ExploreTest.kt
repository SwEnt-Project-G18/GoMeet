package com.github.se.gomeet.ui.mainscreens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.rule.GrantPermissionRule
import com.github.se.gomeet.model.repository.EventRepository
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.junit.Rule
import org.junit.Test

class ExploreTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()
  @get:Rule
  var permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

  @Test
  fun testExplore() {
    composeTestRule.setContent {
      Explore(
          nav = NavigationActions(rememberNavController()),
          eventViewModel = EventViewModel("null", EventRepository(Firebase.firestore)))
    }

    composeTestRule.waitUntil(timeoutMillis = 10000) {
      composeTestRule.onNodeWithTag("Map").isDisplayed()
    }

    composeTestRule.onNodeWithTag("Map").assertIsDisplayed()
    composeTestRule.onNodeWithText("Search").assertIsDisplayed()
    composeTestRule.onNodeWithTag("CurrentLocationButton").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("MapSlider").assertIsDisplayed()
  }
}
