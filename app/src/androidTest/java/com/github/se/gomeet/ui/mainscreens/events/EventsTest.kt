package com.github.se.gomeet.ui.mainscreens.events

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.model.event.location.Location
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
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

    private lateinit var userVM: UserViewModel
    private lateinit var eventVM: EventViewModel

    @JvmStatic
    @BeforeClass
    fun setup() = runBlocking {
      // Create a new user
      Firebase.auth.createUserWithEmailAndPassword(email, pwd).await()
      uid = Firebase.auth.currentUser!!.uid

      userVM = UserViewModel(uid)

      // Add the user to the view model
      userVM.createUserIfNew(
          uid, username, "testfirstname", "testlastname", email, "testphonenumber", "testcountry")
      while (userVM.getUser(uid) == null) {
        TimeUnit.SECONDS.sleep(1)
      }

      // Sign in
      Firebase.auth.signInWithEmailAndPassword(email, pwd).await()

      // Create an event
      eventVM = EventViewModel(uid)
      eventVM.createEvent(
          "title",
          "description",
          Location(0.0, 0.0, "location"),
          LocalDate.of(2026, 1, 1),
          LocalTime.of(9, 17),
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
      while (eventVM.getEvent(eventId) == null) {
        TimeUnit.SECONDS.sleep(1)
      }

      // Add the event to the user's favourites
      userVM.editUser(userVM.getUser(uid)!!.copy(myFavorites = listOf(eventId)))
      while (userVM.getUser(uid)!!.myFavorites.isEmpty()) {
        TimeUnit.SECONDS.sleep(1)
      }
    }

    @AfterClass
    @JvmStatic
    fun tearDown() = runBlocking {
      // Clean up the event
      eventVM.getAllEvents()?.forEach { eventVM.removeEvent(it.eventID) }

      // Clean up the user
      Firebase.auth.currentUser?.delete()?.await()
      userVM.deleteUser(uid)

      return@runBlocking
    }
  }

  @Test
  fun testEvents() {
    composeTestRule.setContent {
      Events(NavigationActions(rememberNavController()), userVM, eventVM)
    }

    composeTestRule.waitForIdle()

    // Wait until the events are loaded
    composeTestRule.waitUntil(timeoutMillis = 10000) {
      composeTestRule.onAllNodesWithText("title")[0].isDisplayed()
    }

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
        .assertCountEquals(2) // The event should be present in all categories
    for (i in 0..1) {
      composeTestRule.onAllNodesWithText("title")[i].assertIsDisplayed()
    }
  }
}
