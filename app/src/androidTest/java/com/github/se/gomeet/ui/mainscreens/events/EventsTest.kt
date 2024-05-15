package com.github.se.gomeet.ui.mainscreens.events

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
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
import org.junit.After
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

class EventsTest {

  @get:Rule val composeTestRule = createComposeRule()

  @After
  fun tearDown() {
    // clean up the event
    runBlocking { eventVM.getAllEvents()?.forEach { eventVM.removeEvent(it.eventID) } }

    // clean up the users
    runBlocking {
      Firebase.auth.currentUser?.delete()
      userVM.deleteUser(uid)
    }
  }

  @Test
  fun testEvents() {
    composeTestRule.setContent {
      Events(
          uid,
          NavigationActions(rememberNavController()),
          UserViewModel(UserRepository(Firebase.firestore)),
          EventViewModel("null", EventRepository(Firebase.firestore)))
    }

    composeTestRule.waitUntil { composeTestRule.onAllNodesWithText("title")[0].isDisplayed() }

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
    composeTestRule.onAllNodesWithText("title").assertCountEquals(3)
    for (i in 0..2) {
      composeTestRule.onAllNodesWithText("title")[i].assertIsDisplayed()
    }
  }

  companion object {

    private const val email = "user@eventstest.com"
    private const val pwd = "123456"
    private var uid = ""
    private const val username = "null"

    private val userVM = UserViewModel(UserRepository(Firebase.firestore))
    private lateinit var eventVM: EventViewModel

    @JvmStatic
    @BeforeClass
    fun setup() {
      TimeUnit.SECONDS.sleep(3)
      runBlocking {
        // create two new users
        var result = Firebase.auth.createUserWithEmailAndPassword(email, pwd)
        while (!result.isComplete) {
          TimeUnit.SECONDS.sleep(1)
        }
        uid = result.result.user!!.uid

        userVM.createUserIfNew(
            uid, username, "testfirstname", "testlastname", email, "testphonenumber", "testcountry")
        TimeUnit.SECONDS.sleep(3)

        result = Firebase.auth.signInWithEmailAndPassword(email, pwd)
        while (!result.isComplete) {
          TimeUnit.SECONDS.sleep(1)
        }
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
            "uid")
        userVM.editUser(userVM.getUser(uid)!!.copy(myFavorites = listOf("uid")))
      }

      TimeUnit.SECONDS.sleep(3)
    }
  }
}
