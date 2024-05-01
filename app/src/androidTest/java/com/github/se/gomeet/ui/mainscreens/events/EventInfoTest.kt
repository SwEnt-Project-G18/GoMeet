package com.github.se.gomeet.ui.mainscreens.events

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.android.gms.maps.model.LatLng
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

    composeTestRule.onNodeWithTag("TopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("EventHeader").assertIsDisplayed()
    composeTestRule.onNodeWithTag("EventImage").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("EventDescription")
        .assertIsDisplayed()
        .assertTextContains(eventDescription)
    composeTestRule.onNodeWithTag("EventButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MapView").assertIsDisplayed()
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
    private val currentUserId = "userid"

    @BeforeClass
    @JvmStatic
    fun setUp() {
      // Set up the user view model
      // Order is important here, since createUserIfNew sets current user to created user (so we
      // need to create the current user last)
      userViewModel.createUserIfNew(organiserId, "", "", "", "", "", "")
      userViewModel.createUserIfNew(currentUserId, "", "", "", "", "", "")

      while (userViewModel.getCurrentUser()?.uid != currentUserId) {
        // Wait for the users to be created
      }
    }

    @AfterClass
    @JvmStatic
    fun tearDown() {
      // Clean up the user view model
      userViewModel.deleteUser(organiserId)
      userViewModel.deleteUser(currentUserId)
    }
  }
}
