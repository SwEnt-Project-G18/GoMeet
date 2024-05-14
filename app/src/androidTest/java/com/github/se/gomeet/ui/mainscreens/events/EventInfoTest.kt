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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
          title = "title",
          eventId = "eventId",
          date = "date",
          time = "time",
          organizerId = "organizerId",
          rating = 0.0,
          description = "description",
          loc = LatLng(0.0, 0.0),
          userViewModel = UserViewModel(UserRepository(Firebase.firestore)),
          eventViewModel = EventViewModel("organizerId", EventRepository(Firebase.firestore)))
    }

    composeTestRule.waitForIdle()

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
            .assertTextContains("description")
            .isDisplayed(),
        { "EventDescription not displayed" })
    assert(
        composeTestRule.onNodeWithTag("EventButton").isDisplayed(), { "EventButton not displayed" })
    assert(composeTestRule.onNodeWithTag("MapView").isDisplayed(), { "MapView not displayed" })
  }
}
