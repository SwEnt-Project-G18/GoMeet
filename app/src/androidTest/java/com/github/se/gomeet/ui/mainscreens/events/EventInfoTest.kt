package com.github.se.gomeet.ui.mainscreens.events

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
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
          userViewModel = userViewModel)
    }

    assert(composeTestRule.onNodeWithTag("TopBar").isDisplayed(), { "TopBar not displayed" })
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
    private val eventTime = "00:00"
    private val eventDescription = "Event Description"
    private val eventLocation = LatLng(0.0, 0.0)
    private val userViewModel = UserViewModel()
    private val eventRating = 4.5
    private lateinit var currentUserId: String

    private val usr = "u@t.com"
    private val pwd = "123456"

    @BeforeClass
    @JvmStatic
    fun setUp() {
      Firebase.auth.createUserWithEmailAndPassword(usr, pwd)
      TimeUnit.SECONDS.sleep(2)
      Firebase.auth.signInWithEmailAndPassword(usr, pwd)
      TimeUnit.SECONDS.sleep(2)
      // Set up the user view model
      // Order is important here, since createUserIfNew sets current user to created user (so we
      // need to create the current user last)
      currentUserId = Firebase.auth.currentUser!!.uid
      runBlocking {
        userViewModel.createUserIfNew(
            organiserId, "testorganiser", "test", "name", "test@email.com", "0123", "Afghanistan")
      }
      userViewModel.createUserIfNew(currentUserId, "a", "b", "c", usr, "4567", "Angola")
      TimeUnit.SECONDS.sleep(2)
    }

    @AfterClass
    @JvmStatic
    fun tearDown() {
      // Clean up the user view model
      Firebase.auth.currentUser!!.delete()

      userViewModel.deleteUser(organiserId)
      userViewModel.deleteUser(currentUserId)
    }
  }
}
