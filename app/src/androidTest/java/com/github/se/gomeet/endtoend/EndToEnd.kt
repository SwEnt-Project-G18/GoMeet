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
import androidx.test.ext.junit.runners.AndroidJUnit4
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
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This end to end test tests that a user can log in with email and password and then create an
 * event and see it in the Events tab
 */
@RunWith(AndroidJUnit4::class)
class EndToEndTest : TestCase() {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  companion object {
    private const val email = "user@test.com"
    private const val pwd = "123456"
    private lateinit var uid: String
    private const val username = "EndToEndTestuser"

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

        // Add the user to the view model
        userVM.createUserIfNew(
            uid, username, "testfirstname", "testlastname", email, "testphonenumber", "testcountry")
        TimeUnit.SECONDS.sleep(3)

        // Sign in
        result = Firebase.auth.signInWithEmailAndPassword(email, pwd)
        while (!result.isComplete) {
          TimeUnit.SECONDS.sleep(1)
        }
        eventVM = EventViewModel(uid, EventRepository(Firebase.firestore))
      }
    }

    @AfterClass
    @JvmStatic
    fun tearDown() {
      runBlocking {
        // Clean up the event
        eventVM.getAllEvents()?.forEach { eventVM.removeEvent(it.eventID) }
        // Clean up the user
        Firebase.auth.currentUser?.delete()
        userVM.deleteUser(uid)
      }
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
      }
    }

    composeTestRule.waitForIdle()

    ComposeScreen.onComposeScreen<CreateEventScreen>(composeTestRule) {
      step("Create an event") {
        composeTestRule.onNodeWithText("Title").assertIsDisplayed().performTextInput("Title")
        composeTestRule
            .onNodeWithText("Description")
            .assertIsDisplayed()
            .performTextInput("Description")
        composeTestRule.onNodeWithText("Location").assertIsDisplayed().performTextInput("test")
        composeTestRule.onNodeWithText("Price").performTextInput("1")
        composeTestRule
            .onNodeWithText("Link")
            .assertIsDisplayed()
            .performTextInput("https://example.com")
        composeTestRule.onNodeWithText("Post").assertIsEnabled().performClick()
        TimeUnit.SECONDS.sleep(3)
      }
    }
    composeTestRule.onNodeWithText("Events").performClick()
    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      composeTestRule.waitForIdle()
      composeTestRule.onAllNodesWithText("Events")[1].performClick()
      composeTestRule.waitUntil(timeoutMillis = 5000) {
        composeTestRule.onAllNodesWithTag("Card")[0].isDisplayed()
      }
      composeTestRule.onAllNodesWithTag("Card")[0].assertIsDisplayed()
    }
  }
}
