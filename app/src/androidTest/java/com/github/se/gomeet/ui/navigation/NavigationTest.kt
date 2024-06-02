package com.github.se.gomeet.ui.navigation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.ui.mainscreens.Trends
import com.github.se.gomeet.ui.mainscreens.create.Create
import com.github.se.gomeet.ui.mainscreens.events.Events
import com.github.se.gomeet.ui.mainscreens.explore.Explore
import com.github.se.gomeet.ui.mainscreens.profile.Profile
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
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
          Explore(nav = NavigationActions(nav), eventVM)
        }
        composable(TOP_LEVEL_DESTINATIONS[1].route) {
          Events(
              nav = NavigationActions(rememberNavController()),
              userViewModel = userVM,
              eventViewModel = eventVM)
        }
        composable(TOP_LEVEL_DESTINATIONS[2].route) {
          Trends(
              nav = NavigationActions(rememberNavController()),
              userViewModel = userVM,
              eventViewModel = eventVM)
        }
        composable(TOP_LEVEL_DESTINATIONS[3].route) { Create(NavigationActions(nav)) }
        composable(TOP_LEVEL_DESTINATIONS[4].route) {
          Profile(NavigationActions(nav), userVM, eventVM)
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
          Explore(nav = NavigationActions(nav), eventVM)
        }
        composable(TOP_LEVEL_DESTINATIONS[1].route) {
          Events(
              nav = NavigationActions(rememberNavController()),
              userViewModel = userVM,
              eventViewModel = eventVM)
        }
        composable(TOP_LEVEL_DESTINATIONS[2].route) {
          Trends(
              nav = NavigationActions(rememberNavController()),
              userViewModel = userVM,
              eventViewModel = eventVM)
        }
        composable(TOP_LEVEL_DESTINATIONS[3].route) { Create(NavigationActions(nav)) }
        composable(TOP_LEVEL_DESTINATIONS[4].route) {
          Profile(NavigationActions(nav), userVM, eventVM)
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
    private lateinit var userVM: UserViewModel
    private lateinit var currentUserId: String
    private lateinit var eventVM: EventViewModel

    private val usr = "u@navtest.com"
    private val pwd = "123456"

    @BeforeClass
    @JvmStatic
    fun setUp() = runBlocking {

      // Create a new user and sign in
      Firebase.auth.createUserWithEmailAndPassword(usr, pwd).await()
      Firebase.auth.signInWithEmailAndPassword(usr, pwd).await()

      // Set up the user view model
      currentUserId = Firebase.auth.currentUser!!.uid
      userVM = UserViewModel(currentUserId)
      eventVM = EventViewModel(currentUserId)
      userVM.createUserIfNew(currentUserId, "a", "b", "c", usr, "4567", "Angola", "")
      TimeUnit.SECONDS.sleep(1)
    }

    @AfterClass
    @JvmStatic
    fun tearDown(): Unit = runBlocking {
      // Clean up the user view model
      UserRepository.removeUser(currentUserId)
      Firebase.auth.currentUser!!.delete().await()
    }
  }
}
