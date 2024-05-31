package com.github.se.gomeet.ui.mainscreens.events

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.model.event.location.Location
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
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
class EditEventTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  companion object {
    private lateinit var eventUid: String
    private val eventVM = EventViewModel("")

    @JvmStatic
    @BeforeClass
    fun setup() = runBlocking {
      // Create an event
      eventVM.createEvent(
          "title",
          "description",
          Location(0.0, 0.0, "location"),
          LocalDate.of(2024, 8, 8),
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
          UserViewModel(""),
          "eventid")

      while (eventVM.getAllEvents()!!.isEmpty()) {
        TimeUnit.SECONDS.sleep(1)
      }

      eventUid = eventVM.getAllEvents()!![0].eventID
    }

    @AfterClass
    @JvmStatic
    fun tearDown() = runBlocking {
      // Clean up the event
      eventVM.getAllEvents()?.forEach { eventVM.removeEvent(it.eventID) }

      return@runBlocking
    }
  }

  @Test
  fun testEditEvent() {
    composeTestRule.setContent {
      EditEvent(NavigationActions(rememberNavController()), eventVM, eventUid) {}
    }

    composeTestRule.waitUntil(timeoutMillis = 10000) {
      composeTestRule.onNodeWithText("Title").isDisplayed()
    }

    composeTestRule.onNodeWithText("Edit Tags").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("Link").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("Price").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("Pick time").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("Pick date").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("Location").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("Description").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("Title").performScrollTo().assertIsDisplayed()
    composeTestRule
        .onNodeWithContentDescription("Event picture")
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Go back").assertIsDisplayed()
    composeTestRule.onNodeWithText("Done").assertIsDisplayed().performClick()
  }
}
