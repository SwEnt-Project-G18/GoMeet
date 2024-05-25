package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
class NotificationsTest {
  @get:Rule val composeTestRule = createComposeRule()

  companion object {

    private const val pwd = "123456"
    private const val email = "notifications@test.com"

    private val uid = "userNotificationsTest"
    private const val otherUid = "AnotherUser"
    private val userVM = UserViewModel(uid)

    private const val eventId = "NotificationsTestEvent"
    private val eventVM = EventViewModel(otherUid)
    private const val eventTitle = "title"

    @BeforeClass
    @JvmStatic
    fun setup() = runBlocking {

      // Create a new user
      userVM.createUserIfNew(
          uid, "NotificationsTest", "firstname", "lastname", email, "+1234567890", "fakecountry")

      userVM.createUserIfNew(
          otherUid, "AnotherUser", "firstname", "lastname", "", "+1234567890", "fakecountry2")

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
          10,
          false,
          emptyList(),
          emptyList(),
          null,
          userVM,
          eventId)

      TimeUnit.SECONDS.sleep(1L)

      // Invite the user to the event
      userVM.gotInvitation(eventId, uid)
      eventVM.sendInvitation(eventId, uid)
      TimeUnit.SECONDS.sleep(1L)
    }

    @AfterClass
    @JvmStatic
    fun tearDown() = runBlocking {

      // Clean up the user and the event
      userVM.deleteUser(uid)
      userVM.deleteUser(otherUid)
      eventVM.removeEvent(eventId)
    }
  }

  @Test
  fun testNotifications() {
    composeTestRule.setContent { Notifications(NavigationActions(rememberNavController()), userVM) }

    composeTestRule.waitForIdle()

    // Test the ui of the screen
    composeTestRule.onNodeWithTag("NotificationsScreen").assertExists()
    composeTestRule.onNodeWithContentDescription("Go back").assertIsDisplayed()
    composeTestRule.onNodeWithText("Messages").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onNodeWithText("Invitations").assertIsDisplayed().assertHasClickAction()

    // Test that the invitation notification widget is correctly displayed
    composeTestRule.waitUntil(timeoutMillis = 10000) {
      composeTestRule.onNodeWithTag("EventDate").isDisplayed()
    }
    composeTestRule.onNodeWithTag("EventDate").assertIsDisplayed()
    composeTestRule.onNodeWithText(eventTitle).assertIsDisplayed()
    composeTestRule.onNodeWithText("30/03/26", substring = true).assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Event Picture").assertIsDisplayed()
    composeTestRule.onNodeWithText("Accept").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onNodeWithText("Decline").assertIsDisplayed().performClick()
  }

  @Test
  fun testAcceptButton() {
    composeTestRule.setContent { Notifications(NavigationActions(rememberNavController()), userVM) }
    composeTestRule.waitUntil(timeoutMillis = 10000) {
      composeTestRule.onNodeWithText("Accept").isDisplayed()
    }
    composeTestRule.onNodeWithText("Accept").assertIsDisplayed().performClick()
  }
}
