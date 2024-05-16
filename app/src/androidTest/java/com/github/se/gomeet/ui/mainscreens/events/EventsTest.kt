package com.github.se.gomeet.ui.mainscreens.events

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.model.event.location.Location
import com.github.se.gomeet.model.repository.EventRepository
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventsTest {
  @get:Rule val composeTestRule = createComposeRule()

  companion object {
    private const val email = "user@eventstest.com"
    private const val pwd = "123456"
    private lateinit var uid: String
    private const val username = "eventstest"

    private const val eventId = "EventsTestEvent"

    private val userVM = UserViewModel(UserRepository(Firebase.firestore))
    private lateinit var eventVM: EventViewModel

    @JvmStatic
    @BeforeClass
    fun setup() {
      runBlocking {
        // Create a new user
        var result = Firebase.auth.createUserWithEmailAndPassword(email, pwd)
        while (!result.isComplete) {
          TimeUnit.SECONDS.sleep(1)
        }
        uid = result.result.user!!.uid

        // Add the user to the view model
        userVM.createUserIfNew(
            uid, username, "testfirstname", "testlastname", email, "testphonenumber", "testcountry")
        TimeUnit.SECONDS.sleep(3)

        // Sign in
        result = Firebase.auth.signInWithEmailAndPassword(email, pwd)
        while (!result.isComplete) {
          TimeUnit.SECONDS.sleep(1)
        }

        // Create an event
        eventVM = EventViewModel(uid, EventRepository(Firebase.firestore))
        eventVM.createEvent(
            "title",
            "description",
            Location(0.0, 0.0, "location"),
            LocalDate.of(2026, 1, 1),
            LocalTime.now(),
            0.0,
            "url",
            emptyList(),
            emptyList(),
            emptyList(),
            1,
            true,
            emptyList(),
            emptyList(),
            null,
            userVM,
            eventId)

        // Add the event to the user's favourites
        userVM.editUser(userVM.getUser(uid)!!.copy(myFavorites = listOf(eventId)))
        while (userVM.getUser(uid)!!.myFavorites.isEmpty()) {
          TimeUnit.SECONDS.sleep(1)
        }
      }
    }

    @AfterClass
    @JvmStatic
    fun tearDown() {
      runBlocking {
        // Clean up the event
        eventVM.getAllEvents()?.forEach {
          eventVM.removeEvent(it.eventID)

          // Clean up the user
          Firebase.auth.currentUser?.delete()
          userVM.deleteUser(uid)
        }
      }
    }
  }

  @Test
  fun testEvents() {
    composeTestRule.setContent {
      Events(
          uid,
          NavigationActions(rememberNavController()),
          UserViewModel(UserRepository(Firebase.firestore)),
          eventVM)
    }

    composeTestRule.waitForIdle()

    // Wait until the events are loaded
    composeTestRule.waitUntil { composeTestRule.onAllNodesWithText("title")[0].isDisplayed() }

    // Verify that the ui is correctly displayed
    composeTestRule.onNodeWithText("Search").assertIsDisplayed()
    composeTestRule.onNodeWithTag("JoinedButton").assertIsDisplayed().performClick().performClick()
    composeTestRule
        .onNodeWithTag("FavouritesButton")
        .assertIsDisplayed()
        .performClick()
        .performClick()
    composeTestRule
        .onNodeWithTag("MyEventsButton")
        .assertIsDisplayed()
        .performClick()
        .performClick()
    composeTestRule.onNodeWithTag("JoinedTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("FavouritesTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MyEventsTitle").assertIsDisplayed()
    composeTestRule
        .onAllNodesWithText("title")
        .assertCountEquals(3) // The event should be present in all categories
    for (i in 0..2) {
      composeTestRule.onAllNodesWithText("title")[i].assertIsDisplayed()
    }
  }
}
