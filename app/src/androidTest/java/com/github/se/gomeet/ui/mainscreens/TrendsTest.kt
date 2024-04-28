package com.github.se.gomeet.ui.mainscreens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.junit.Before
import org.junit.BeforeClass
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
      Trends(NavigationActions(navController))
    }

    rule.onAllNodesWithText("Trends").apply {
      fetchSemanticsNodes().forEachIndexed { i, _ -> get(i).assertIsDisplayed() }
    }
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun setUpClass() {
      Firebase.firestore.useEmulator("10.0.2.2", 8080)
      Firebase.storage.useEmulator("10.0.2.2", 9199)
    }
  }
}
