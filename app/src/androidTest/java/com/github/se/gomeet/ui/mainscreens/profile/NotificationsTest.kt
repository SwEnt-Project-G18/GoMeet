package com.github.se.gomeet.ui.mainscreens.profile

import android.util.Log
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.model.event.location.Location
import com.github.se.gomeet.ui.mainscreens.notifications.Notifications
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
class NotificationsTest {
  @get:Rule val composeTestRule = createComposeRule()

  companion object {
    private const val uid1 = "NotificationsTestUser"
    private const val uid2 = "AnotherUser"
    private var userVM = UserViewModel(uid2)

    private const val eventId = "NotificationsTestEvent"
    private val eventVM = EventViewModel("AnotherUser")
    private const val eventTitle = "title"

    @BeforeClass
    @JvmStatic
    fun setup() = runBlocking {
      // Create a new user
      userVM.createUserIfNew(
          uid1,
          "NotificationsTest",
          "firstname",
          "lastname",
          "notifications@test.com",
          "+1234567890",
          "fakecountry")
      while (userVM.getUser(uid1) == null) {
        TimeUnit.SECONDS.sleep(1)
      }

      // Create a new event
      eventVM.createEvent(
          eventTitle,
          "description",
          Location(0.0, 0.0, "location"),
          LocalDate.of(2026, 3, 30),
          LocalTime.now(),
          0.0,
          "url",
          emptyList(),
          emptyList(),
          emptyList(),
          1,
          false,
          emptyList(),
          emptyList(),
          null,
          userVM,
          eventId)
      while (eventVM.getEvent(eventId) == null) {
        TimeUnit.SECONDS.sleep(1)
      }

      // Invite the user to the event
      userVM = UserViewModel(uid1)
      userVM.gotInvitation(eventId, uid1)
      eventVM.sendInvitation(eventVM.getEvent(eventId)!!, uid1)
      while (userVM.getUser(uid1)!!.pendingRequests.isEmpty() ||
          eventVM.getEvent(eventId)!!.pendingParticipants.isEmpty()) {
        TimeUnit.SECONDS.sleep(1)
        Log.e("Qwe", "qwe")
      }
    }

    @AfterClass
    @JvmStatic
    fun tearDown() = runBlocking {
      // Clean up the user and the event
      userVM.deleteUser(uid1)
      eventVM.removeEvent(eventId)

      return@runBlocking
    }
  }

  @Test
  fun testNotifications() {
    composeTestRule.setContent { Notifications(NavigationActions(rememberNavController()), userVM) }

    composeTestRule.waitUntil(timeoutMillis = 10000) {
      composeTestRule.onNodeWithText("Accept").isDisplayed()
    }

    // Test the ui of the screen
    composeTestRule.onNodeWithTag("NotificationsScreen").assertExists()
    // composeTestRule.onNodeWithContentDescription("Go back").assertIsDisplayed()
    composeTestRule.onNodeWithText("Messages").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onNodeWithText("Invitations").assertIsDisplayed().assertHasClickAction()

    // Test that the invitation notification widget is correctly displayed
    composeTestRule.onNodeWithText(eventTitle, substring = true).assertIsDisplayed()
    composeTestRule.onNodeWithText("30/3/2026", substring = true).assertIsDisplayed()
    composeTestRule.onNodeWithText("Accept").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onNodeWithText("Decline").assertIsDisplayed().performClick()
  }
}
