package com.github.se.gomeet.ui.navigation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.ui.mainscreens.Events
import com.github.se.gomeet.ui.mainscreens.Explore
import com.github.se.gomeet.ui.mainscreens.Profile
import com.github.se.gomeet.ui.mainscreens.Trends
import com.github.se.gomeet.ui.mainscreens.create.Create
import com.github.se.gomeet.viewmodel.EventViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun testNavigateTo() {

    composeTestRule.setContent {
      val nav = rememberNavController()
      NavHost(navController = nav, startDestination = Route.EVENTS) {
        composable(TOP_LEVEL_DESTINATIONS[0].route) { Explore(nav = NavigationActions(nav)) }
        composable(TOP_LEVEL_DESTINATIONS[1].route) { Events(NavigationActions(nav), EventViewModel()) }
        composable(TOP_LEVEL_DESTINATIONS[2].route) { Trends(NavigationActions(nav)) }
        composable(TOP_LEVEL_DESTINATIONS[3].route) { Create(NavigationActions(nav)) }
        composable(TOP_LEVEL_DESTINATIONS[4].route) { Profile(NavigationActions(nav)) }
        // Add more destinations as needed
      }

      val navActions = NavigationActions(nav)

      for (dest in TOP_LEVEL_DESTINATIONS) {
        runBlocking { navActions.navigateTo(dest) }
        assert(nav.currentDestination?.route == dest.route)
      }
    }
  }

  @Test
  fun testGoBack() {

    composeTestRule.setContent {
      val nav = rememberNavController()
      NavHost(navController = nav, startDestination = TOP_LEVEL_DESTINATIONS[0].route) {
        composable(TOP_LEVEL_DESTINATIONS[0].route) { Explore(nav = NavigationActions(nav)) }
        composable(TOP_LEVEL_DESTINATIONS[1].route) { Events(NavigationActions(nav), EventViewModel()) }
        composable(TOP_LEVEL_DESTINATIONS[2].route) { Trends(NavigationActions(nav)) }
        composable(TOP_LEVEL_DESTINATIONS[3].route) { Create(NavigationActions(nav)) }
        composable(TOP_LEVEL_DESTINATIONS[4].route) { Profile(NavigationActions(nav)) }
      }

      val navActions = NavigationActions(nav)
      val backDest = nav.currentDestination

      // Drop first destination, since navigating to it has no effect and messes up the test
      for (dest in TOP_LEVEL_DESTINATIONS.drop(1)) {
        runBlocking { navActions.navigateTo(dest) }
        runBlocking { navActions.goBack() }
        assert(nav.currentDestination?.route == backDest?.route)
      }
    }
  }
}
