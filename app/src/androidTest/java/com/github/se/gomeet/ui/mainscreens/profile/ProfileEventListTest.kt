package com.github.se.gomeet.ui.mainscreens.profile

import androidx.activity.ComponentActivity
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.model.event.Event
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
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileEventListTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  companion object {
    private const val email = "profileeventlist@test.com"
    private const val pwd = "123456"
    private lateinit var uid: String
    private const val username = "ProfileEventList"

    private const val eventId = "ProfileEventListEvent"
    private lateinit var event: Event

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

        // Add the user to view model
        userVM.createUserIfNew(
            uid, username, "testfirstname", "testlastname", email, "testphonenumber", "testcountry")
        while (userVM.getUser(uid) == null) {
          TimeUnit.SECONDS.sleep(1)
        }

        // Sign in
        result = Firebase.auth.signInWithEmailAndPassword(email, pwd)
        while (!result.isComplete) {
          TimeUnit.SECONDS.sleep(1)
        }

        // Create an Event
        eventVM = EventViewModel(uid, EventRepository(Firebase.firestore))
        eventVM.createEvent(
            "title",
            "description",
            Location(0.0, 0.0, "location"),
            LocalDate.of(2026, 1, 1),
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
        event = eventVM.getEvent(eventId)!!
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
  fun testProfileEventList() {
    composeTestRule.setContent {
      ProfileEventsList(
          "title",
          rememberLazyListState(),
          mutableListOf(event),
          NavigationActions(rememberNavController()))
    }

    // Wait for the page to load
    composeTestRule.waitUntil(timeoutMillis = 10000) {
      composeTestRule.onNodeWithTag("EventsListItems").isDisplayed()
    }

    // Test that the ui is correctly displayed
    composeTestRule.onNodeWithTag("EventsListItems").assertIsDisplayed()
    composeTestRule.onNodeWithTag("EventsListHeader").assertIsDisplayed()
    composeTestRule.onNodeWithText("2026-01-01").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("description").assertIsDisplayed()
  }
}
