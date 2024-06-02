package com.github.se.gomeet.ui.mainscreens

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.location.Location
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.github.kakaocup.kakao.common.utilities.getResourceString
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchModuleTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()
  val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

  companion object {
    private const val email = "user@searchmoduletest.com"
    private const val pwd = "123456"
    private lateinit var uid: String
    private const val username = "searchmoduletest"
    private const val firstName = "smtfirstname"
    private const val lastName = "smtlastname"

    private const val eventTitle = "TestEvent"
    private const val eventId = "TestEvent"
    private const val eventDescription = "description"

    private lateinit var userVM: UserViewModel
    private lateinit var eventVM: EventViewModel

    @JvmStatic
    @BeforeClass
    fun setup() = runBlocking {
      // Create a new user
      Firebase.auth.createUserWithEmailAndPassword(email, pwd).await()

      uid = Firebase.auth.currentUser!!.uid

      userVM = UserViewModel(uid)

      // Add the user to the view model
      userVM.createUserIfNew(
          uid,
          username,
          firstName,
          lastName,
          email,
          "testphonenumber",
          "testcountry",
          favorites = listOf(eventId))
      while (userVM.getUser(uid) == null) {
        TimeUnit.SECONDS.sleep(2)
      }

      // Sign in
      Firebase.auth.signInWithEmailAndPassword(email, pwd).await()

      // Create an event
      eventVM = EventViewModel(uid)
      eventVM.createEvent(
          eventTitle,
          eventDescription,
          Location(0.0, 0.0, "location"),
          LocalDate.of(2024, 8, 8),
          LocalTime.of(9, 17),
          0.0,
          "url",
          emptyList(),
          listOf(uid),
          emptyList(),
          1,
          true,
          emptyList(),
          emptyList(),
          null,
          userVM,
          eventId)
    }

    @AfterClass
    @JvmStatic
    fun tearDown() = runBlocking {
      // Clean up the events
      eventVM.getAllEvents { events -> events?.forEach { eventVM.removeEvent(it.eventID) } }

      // Clean up the user
      Firebase.auth.currentUser?.delete()?.await()
      userVM.deleteUser(uid)

      return@runBlocking
    }
  }

  @Test
  fun testSearchModule() {
    composeTestRule.setContent {
      SearchModule(
          nav = NavigationActions(rememberNavController()),
          MaterialTheme.colorScheme.primaryContainer,
          MaterialTheme.colorScheme.onBackground,
          "")
    }

    // Verify that the ui is correctly displayed
    composeTestRule
        .onNodeWithText(getResourceString(R.string.search_bar_placeholder))
        .assertIsDisplayed()
        .performTextInput(username)
    device.pressEnter()

    composeTestRule.waitUntil(timeoutMillis = 10000) {
      composeTestRule.onNodeWithContentDescription("User Icon").isDisplayed()
    }

    composeTestRule.onNodeWithContentDescription("User Icon").assertIsDisplayed()
    composeTestRule.onAllNodesWithText(username, substring = true)[1].assertIsDisplayed()
    composeTestRule.onNodeWithText(firstName, substring = true).assertIsDisplayed()
    composeTestRule.onNodeWithText(lastName, substring = true).assertIsDisplayed()

    composeTestRule.onAllNodesWithText(username, substring = true)[0].performTextClearance()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.search_bar_placeholder))
        .performTextInput(eventTitle)
    device.pressEnter()

    composeTestRule.waitUntil(timeoutMillis = 10000) {
      composeTestRule.onNodeWithContentDescription("Event Icon").isDisplayed()
    }

    composeTestRule.onNodeWithContentDescription("Event Icon").assertIsDisplayed()
    composeTestRule.onAllNodesWithText(eventTitle)[1].assertIsDisplayed()
    composeTestRule.onNodeWithText(eventDescription).assertIsDisplayed()
  }
}
