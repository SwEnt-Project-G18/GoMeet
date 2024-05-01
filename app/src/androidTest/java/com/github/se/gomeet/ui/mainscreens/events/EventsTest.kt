package com.github.se.gomeet.ui.mainscreens.events

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.model.event.location.Location
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventsTest {

  @get:Rule val composeTestRule = createComposeRule()

  @After
  fun tearDown() {
    // clean up the event
    runBlocking { eventViewModel.getAllEvents()?.forEach { eventViewModel.removeEvent(it.uid) } }
    // clean up the user
    Firebase.auth.currentUser?.delete()
  }

  @Test
  fun eventsScreen_RenderingCorrectness() {
    // Test rendering correctness with events available
    composeTestRule.setContent {
      Events(
          currentUser = "test",
          nav = NavigationActions(rememberNavController()),
          userViewModel = userViewModel,
          eventViewModel = eventViewModel)
    }

    composeTestRule.onNode(hasText("My events")).assertIsDisplayed()
    composeTestRule.onAllNodesWithText("Favourites")[0].assertIsDisplayed()
    composeTestRule.onAllNodesWithText("Favourites")[1].assertIsDisplayed()
    composeTestRule.onAllNodesWithText("Joined Events")[0].assertIsDisplayed()
    composeTestRule.onAllNodesWithText("Joined Events")[0].assertIsDisplayed()
    composeTestRule.onAllNodesWithText("My events")[0].assertIsDisplayed()
  }

  @Test
  fun eventsScreen_FilterButtonClick() {
    // Test button click handling
    composeTestRule.setContent {
      Events(
          currentUser = "NEEGn5cbkJZDXaezeGdfd2D4u6b2",
          nav = NavigationActions(rememberNavController()),
          userViewModel = userViewModel,
          eventViewModel = eventViewModel)
    }

    composeTestRule.onNodeWithText("JoinedEvents").performClick()
    composeTestRule.onNodeWithText("Favourites").performClick()
    composeTestRule.onNodeWithText("My events").performClick()
  }

  @Test
  fun eventsScreen_AsyncBehavior() {
    // Test asynchronous behavior of fetching events
    runBlocking(Dispatchers.IO) {
      // Add a mock event to the view model
      eventViewModel.createEvent(
          title = "Test Event",
          description = "Test description",
          location = Location(46.5190557, 6.5555216, "EPFL Campus"), // Provide a valid location
          date = LocalDate.now(), // Provide a valid date
          price = 10.0,
          url = "",
          participants = emptyList(),
          visibleToIfPrivate = emptyList(),
          maxParticipants = 0,
          public = true,
          tags = emptyList(),
          images = emptyList(),
          imageUri = null,
          userViewModel = UserViewModel(),
          uid = "testevent")
    }

    composeTestRule.setContent {
      Events(
          currentUser = "NEEGn5cbkJZDXaezeGdfd2D4u6b2",
          nav = NavigationActions(rememberNavController()),
          userViewModel = userViewModel,
          eventViewModel = eventViewModel)
    }
  }

  companion object {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var userViewModel: UserViewModel

    private const val email = "user@eventstest.com"
    private const val pwd = "123456"
    private var uid = ""

    @JvmStatic
    @BeforeClass
    fun setup() {
      TimeUnit.SECONDS.sleep(3)
      // create a new user
      var result = Firebase.auth.createUserWithEmailAndPassword(email, pwd)
      while (!result.isComplete) {
        TimeUnit.SECONDS.sleep(1)
      }
      uid = result.result.user!!.uid

      runBlocking { userViewModel.createUserIfNew(uid, "events_test_user") }

      // sign in as the new user
      result = Firebase.auth.signInWithEmailAndPassword(email, pwd)
      while (!result.isComplete) {
        TimeUnit.SECONDS.sleep(1)
      }

      eventViewModel = EventViewModel(uid)
      userViewModel = UserViewModel()
    }
  }
}
