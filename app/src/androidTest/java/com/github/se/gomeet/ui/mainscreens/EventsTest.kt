package com.github.se.gomeet.ui.mainscreens

import android.net.Uri
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.model.event.location.Location
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

class EventsTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun eventsScreen_RenderingCorrectness() {
    // Test rendering correctness with events available
    composeTestRule.setContent {
      Events(nav = NavigationActions(rememberNavController()), eventViewModel = EventViewModel())
    }

    composeTestRule.onNode(hasText("My events")).assertIsDisplayed()
    composeTestRule.onAllNodesWithText("Favourites")[0].assertIsDisplayed()
    composeTestRule.onAllNodesWithText("Favourites")[1].assertIsDisplayed()
    composeTestRule.onAllNodesWithText("My tickets")[0].assertIsDisplayed()
    composeTestRule.onAllNodesWithText("My Tickets")[0].assertIsDisplayed()
    composeTestRule.onAllNodesWithText("My events")[0].assertIsDisplayed()
  }

  @Test
  fun eventsScreen_FilterButtonClick() {
    // Test button click handling
    composeTestRule.setContent {
      Events(nav = NavigationActions(rememberNavController()), eventViewModel = EventViewModel())
    }

    composeTestRule.onNodeWithText("My tickets").performClick()
    composeTestRule.onNodeWithText("Favourites").performClick()
    composeTestRule.onNodeWithText("My events").performClick()
  }

  @Test
  fun eventsScreen_AsyncBehavior() {
    // Test asynchronous behavior of fetching events
    val eventViewModel = EventViewModel()
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
          imageUri = null)
    }

    composeTestRule.setContent {
      Events(nav = NavigationActions(rememberNavController()), eventViewModel = EventViewModel())
    }
  }

    companion object {
        @JvmStatic
        @BeforeClass
        fun setup(): Unit {
            Firebase.firestore.useEmulator("10.0.2.2", 8080)
            Firebase.storage.useEmulator("10.0.2.2", 9199)
        }
    }
}
