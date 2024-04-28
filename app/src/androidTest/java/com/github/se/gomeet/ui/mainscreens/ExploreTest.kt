package com.github.se.gomeet.ui.mainscreens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExploreTest {
  @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()
  @get:Rule
  var permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

  @Test
  fun uiElementsDisplayed() {
    lateinit var navController: NavHostController

    rule.setContent {
      navController = rememberNavController()
      Explore(nav = NavigationActions(navController), eventViewModel = EventViewModel())
    }

    rule.waitUntil(timeoutMillis = 10000) { rule.onNodeWithTag("Map").isDisplayed() }

    rule.onNodeWithTag("Map").assertIsDisplayed()
    rule.onNodeWithText("Search").assertIsDisplayed()
    rule.onNodeWithTag("CurrentLocationButton").assertIsDisplayed().performClick()
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun setup() {
      Firebase.firestore.useEmulator("10.0.2.2", 8080)
      Firebase.storage.useEmulator("10.0.2.2", 9199)
    }
  }
}
