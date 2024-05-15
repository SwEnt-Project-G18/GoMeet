package com.github.se.gomeet.ui.mainscreens.events

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.model.repository.EventRepository
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

class EventInfoTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testEventInfo() {
    // Test the EventInfo screen
    composeTestRule.setContent {
      MyEventInfo(
          nav = NavigationActions(rememberNavController()),
          title = eventTitle,
          eventId = eventId,
          date = eventDate,
          time = eventTime,
          organizerId = organiserId,
          rating = eventRating,
          description = eventDescription,
          loc = eventLocation,
          userViewModel = userVM,
          eventViewModel = eventVM)
    }

    assert(composeTestRule.onNodeWithTag("TopBar").isDisplayed(), { "TopBar not displayed" })
    composeTestRule.waitUntil(timeoutMillis = 10000) {
      composeTestRule.onNodeWithTag("EventHeader").isDisplayed()
    }
    assert(
        composeTestRule.onNodeWithTag("EventHeader").isDisplayed(), { "EventHeader not displayed" })
    assert(
        composeTestRule.onNodeWithTag("EventImage").isDisplayed(), { "EventImage not displayed" })
    assert(
        composeTestRule
            .onNodeWithTag("EventDescription")
            .assertTextContains(eventDescription)
            .isDisplayed(),
        { "EventDescription not displayed" })
    assert(
        composeTestRule.onNodeWithTag("EventButton").isDisplayed(), { "EventButton not displayed" })
    assert(composeTestRule.onNodeWithTag("MapView").isDisplayed(), { "MapView not displayed" })
  }

  companion object {

    private val eventTitle = "Event Title"
    private val eventId = "eventid"
    private val eventDate = "2024-05-01"
    private val organiserId = "organiserid"
    private val eventTime = "23:40"
    private val eventDescription = "Event Description"
    private val eventLocation = LatLng(0.0, 0.0)
    private val userVM = UserViewModel(UserRepository(Firebase.firestore))
    private val eventVM = EventViewModel(organiserId, EventRepository(Firebase.firestore))
    private val eventRating = 4.5
    private lateinit var currentUserId: String

    private val usr = "u@t.com"
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
      runBlocking {
        userVM.createUserIfNew(
            organiserId, "testorganiser", "test", "name", "test@email.com", "0123", "Afghanistan")
      }
      userVM.createUserIfNew(currentUserId, "a", "b", "c", usr, "4567", "Angola")
      TimeUnit.SECONDS.sleep(3)
    }

    @AfterClass
    @JvmStatic
    fun tearDown() {
      // Clean up the user view model
      Firebase.auth.currentUser!!.delete()

      userVM.deleteUser(organiserId)
      userVM.deleteUser(currentUserId)
    }
  }
}
