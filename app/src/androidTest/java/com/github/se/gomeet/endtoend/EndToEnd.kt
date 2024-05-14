package com.github.se.gomeet.endtoend

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.github.se.gomeet.MainActivity
import com.github.se.gomeet.model.repository.EventRepository
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.screens.CreateEventScreen
import com.github.se.gomeet.screens.CreateScreen
import com.github.se.gomeet.screens.EventsScreen
import com.github.se.gomeet.screens.LoginScreenScreen
import com.github.se.gomeet.screens.WelcomeScreenScreen
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

/**
 * This end to end test tests that a user can log in with email and password and then create an
 * event
 */
class EndToEndTest : TestCase() {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @After
  fun tearDown() {
    // Clean up the event
    runBlocking { eventVM.getAllEvents()?.forEach { eventVM.removeEvent(it.eventID) } }

    // Clean up the user
    runBlocking {
      Firebase.auth.currentUser?.delete()
      userVM.deleteUser(uid)
    }
  }

  @Test
  fun test() = run {
    ComposeScreen.onComposeScreen<WelcomeScreenScreen>(composeTestRule) {
      step("Click on the log in button") {
        composeTestRule.onNodeWithText("Log In").assertIsDisplayed().performClick()
      }
    }

    ComposeScreen.onComposeScreen<LoginScreenScreen>(composeTestRule) {
      step("Log in with email and password") {
        composeTestRule.onNodeWithText("Log in").assertIsDisplayed().assertIsNotEnabled()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed().performTextInput(email)
        composeTestRule.onNodeWithText("Password").assertIsDisplayed().performTextInput(pwd)
        composeTestRule.onNodeWithText("Log in").assertIsEnabled().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil(timeoutMillis = 10000) {
          composeTestRule.onNodeWithTag("CreateUI").isDisplayed()
        }
      }
    }

    ComposeScreen.onComposeScreen<CreateScreen>(composeTestRule) {
      step("Select which type of event to create") {
        composeTestRule.onNodeWithText("Public").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()
      }
    }

    ComposeScreen.onComposeScreen<CreateEventScreen>(composeTestRule) {
      step("Create an event") {
        composeTestRule.onNodeWithText("Title").performTextInput("test")
        composeTestRule.onNodeWithText("Description").performTextInput("test")
        composeTestRule.onNodeWithText("Location").performTextInput("test")
        composeTestRule.onNodeWithTag("DropdownMenu").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText("Date").performTextInput("2025-06-06")
        composeTestRule.onNodeWithText("Price").performTextInput("1")
        composeTestRule.onNodeWithText("Link").performTextInput("1")
        composeTestRule.onNodeWithTag("TagsButton").performClick()
        composeTestRule.onNodeWithTag("TagList").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText("Save").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText("Edit Tags").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add Image").assertIsDisplayed()
        composeTestRule.onNodeWithText("Post").performClick()
        composeTestRule.waitForIdle()
        TimeUnit.SECONDS.sleep(3)
      }
    }

    composeTestRule.onNodeWithText("Events").performClick()
    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      composeTestRule.waitForIdle()
      composeTestRule.onAllNodesWithText("Events")[1].performClick()
      composeTestRule.waitUntil(timeoutMillis = 5000) {
        composeTestRule.onAllNodesWithTag("EventWidget")[0].isDisplayed()
      }
      composeTestRule.onAllNodesWithTag("EventWidget")[0].assertIsDisplayed()
    }
  }

  companion object {

    private const val email = "user@endtoendtest.com"
    private const val pwd = "123456"
    private var uid = "null"
    private const val username = "endtoendtestuser"

    private val userVM = UserViewModel(UserRepository(Firebase.firestore))
    private lateinit var eventVM: EventViewModel

    @JvmStatic
    @BeforeClass
    fun setup() {
      runBlocking {
        // create a new user
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
      }

      TimeUnit.SECONDS.sleep(3)
    }
  }
}
