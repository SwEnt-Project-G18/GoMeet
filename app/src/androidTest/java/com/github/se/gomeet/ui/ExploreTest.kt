package com.github.se.gomeet.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.ui.mainscreens.Explore
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventViewModel
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExploreTest {

  @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun testExploreScreen() {
    lateinit var nav: NavHostController

    rule.setContent {
      nav = rememberNavController()
      Explore(NavigationActions(nav), EventViewModel())
    }

    // Wait for the map to load
    val countdown = CountDownLatch(1)
    countdown.await(3, TimeUnit.SECONDS)

    rule.onNodeWithTag("Map").assertIsDisplayed()
  }
}
