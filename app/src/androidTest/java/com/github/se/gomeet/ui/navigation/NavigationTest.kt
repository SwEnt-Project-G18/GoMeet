package com.github.se.gomeet.ui.navigation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.ui.mainscreens.Explore
import com.github.se.gomeet.ui.mainscreens.Trends
import com.github.se.gomeet.ui.mainscreens.create.Create
import com.github.se.gomeet.ui.mainscreens.events.Events
import com.github.se.gomeet.ui.mainscreens.profile.Profile
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import org.junit.AfterClass
import org.junit.BeforeClass
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
        composable(TOP_LEVEL_DESTINATIONS[0].route) {
          Explore(nav = NavigationActions(nav), EventViewModel(null))
        }
        composable(TOP_LEVEL_DESTINATIONS[1].route) {
          Events(
              currentUser = currentUserId,
              nav = NavigationActions(rememberNavController()),
              userViewModel = UserViewModel(),
              eventViewModel = EventViewModel("NEEGn5cbkJZDXaezeGdfd2D4u6b2"))
        }
        composable(TOP_LEVEL_DESTINATIONS[2].route) {
          Trends(
              currentUserId = currentUserId,
              nav = NavigationActions(rememberNavController()),
              userViewModel = UserViewModel(),
              eventViewModel = EventViewModel("NEEGn5cbkJZDXaezeGdfd2D4u6b2"))
        }
        composable(TOP_LEVEL_DESTINATIONS[3].route) { Create(NavigationActions(nav)) }
        composable(TOP_LEVEL_DESTINATIONS[4].route) {
          Profile(
              NavigationActions(nav),
              userId = currentUserId,
              UserViewModel(),
              EventViewModel(currentUserId))
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
      NavHost(navController = nav, startDestination = TOP_LEVEL_DESTINATIONS[0].route) {
        composable(TOP_LEVEL_DESTINATIONS[0].route) {
          Explore(nav = NavigationActions(nav), EventViewModel(null))
        }
        composable(TOP_LEVEL_DESTINATIONS[1].route) {
          Events(
              currentUser = currentUserId,
              nav = NavigationActions(rememberNavController()),
              userViewModel = UserViewModel(),
              eventViewModel = EventViewModel("NEEGn5cbkJZDXaezeGdfd2D4u6b2"))
        }
        composable(TOP_LEVEL_DESTINATIONS[2].route) {
          Trends(
              currentUserId = currentUserId,
              nav = NavigationActions(rememberNavController()),
              userViewModel = UserViewModel(),
              eventViewModel = EventViewModel("NEEGn5cbkJZDXaezeGdfd2D4u6b2"))
        }
        composable(TOP_LEVEL_DESTINATIONS[3].route) { Create(NavigationActions(nav)) }
        composable(TOP_LEVEL_DESTINATIONS[4].route) {
          Profile(
              NavigationActions(nav),
              userId = currentUserId,
              UserViewModel(),
              EventViewModel(currentUserId))
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

  companion object {
    private val userVM = UserViewModel()
    private lateinit var currentUserId: String

    private val usr = "u@navtest.com"
    private val pwd = "123456"

    @BeforeClass
    @JvmStatic
    fun setUp() {
      TimeUnit.SECONDS.sleep(3)

      // Create a new user and sign in
      var result = Firebase.auth.createUserWithEmailAndPassword(usr, pwd)
      while (!result.isComplete) {
        TimeUnit.SECONDS.sleep(1)
      }
      result = Firebase.auth.signInWithEmailAndPassword(usr, pwd)
      while (!result.isComplete) {
        TimeUnit.SECONDS.sleep(1)
      }

      // Set up the user view model
      // Order is important here, since createUserIfNew sets current user to created user (so we
      // need to create the current user last)
      currentUserId = Firebase.auth.currentUser!!.uid
      userVM.createUserIfNew(currentUserId, "a", "b", "c", usr, "4567", "Angola", "")
      TimeUnit.SECONDS.sleep(3)
    }

    @AfterClass
    @JvmStatic
    fun tearDown() {
      // Clean up the user view model
      Firebase.auth.currentUser!!.delete()
      userVM.deleteUser(currentUserId)
    }
  }
}
