package com.github.se.gomeet.ui.navigation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.model.repository.EventRepository
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.ui.mainscreens.Explore
import com.github.se.gomeet.ui.mainscreens.Trends
import com.github.se.gomeet.ui.mainscreens.create.Create
import com.github.se.gomeet.ui.mainscreens.events.Events
import com.github.se.gomeet.ui.mainscreens.profile.Profile
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
      val userRepository = UserRepository(Firebase.firestore)
      val eventRepository = EventRepository(Firebase.firestore)
      NavHost(navController = nav, startDestination = Route.EVENTS) {
        composable(TOP_LEVEL_DESTINATIONS[0].route) {
          Explore(nav = NavigationActions(nav), EventViewModel(null, eventRepository))
        }
        composable(TOP_LEVEL_DESTINATIONS[1].route) {
          Events(
              currentUser = "NEEGn5cbkJZDXaezeGdfd2D4u6b2",
              nav = NavigationActions(rememberNavController()),
              userViewModel = UserViewModel(userRepository),
              eventViewModel = EventViewModel("NEEGn5cbkJZDXaezeGdfd2D4u6b2", eventRepository))
        }
        composable(TOP_LEVEL_DESTINATIONS[2].route) {
          Trends(
              currentUser = "NEEGn5cbkJZDXaezeGdfd2D4u6b2",
              nav = NavigationActions(rememberNavController()),
              userViewModel = UserViewModel(userRepository),
              eventViewModel = EventViewModel("NEEGn5cbkJZDXaezeGdfd2D4u6b2", eventRepository))
        }
        composable(TOP_LEVEL_DESTINATIONS[3].route) { Create(NavigationActions(nav)) }
        composable(TOP_LEVEL_DESTINATIONS[4].route) {
          Profile(
              NavigationActions(nav),
              userId = "1234",
              UserViewModel(userRepository),
              EventViewModel("1234", eventRepository))
        }
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
      val userRepository = UserRepository(Firebase.firestore)
      val eventRepository = EventRepository(Firebase.firestore)
      NavHost(navController = nav, startDestination = TOP_LEVEL_DESTINATIONS[0].route) {
        composable(TOP_LEVEL_DESTINATIONS[0].route) {
          Explore(nav = NavigationActions(nav), EventViewModel(null, eventRepository))
        }
        composable(TOP_LEVEL_DESTINATIONS[1].route) {
          Events(
              currentUser = "NEEGn5cbkJZDXaezeGdfd2D4u6b2",
              nav = NavigationActions(rememberNavController()),
              userViewModel = UserViewModel(userRepository),
              eventViewModel = EventViewModel("NEEGn5cbkJZDXaezeGdfd2D4u6b2", eventRepository))
        }
        composable(TOP_LEVEL_DESTINATIONS[2].route) {
          Trends(
              currentUser = "NEEGn5cbkJZDXaezeGdfd2D4u6b2",
              nav = NavigationActions(rememberNavController()),
              userViewModel = UserViewModel(userRepository),
              eventViewModel = EventViewModel("NEEGn5cbkJZDXaezeGdfd2D4u6b2", eventRepository))
        }
        composable(TOP_LEVEL_DESTINATIONS[3].route) { Create(NavigationActions(nav)) }
        composable(TOP_LEVEL_DESTINATIONS[4].route) {
          Profile(
              NavigationActions(nav),
              userId = "TestUser",
              UserViewModel(userRepository),
              EventViewModel("TestUser", eventRepository))
        }
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
