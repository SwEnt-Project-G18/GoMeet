package com.github.se.gomeet.endtoend

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.github.se.gomeet.MainActivity
import com.github.se.gomeet.model.repository.EventRepository
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.screens.CreateEventScreen
import com.github.se.gomeet.screens.CreateScreen
import com.github.se.gomeet.screens.EventInfoScreen
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
 * This end to end test tests that a user can log in with email and password, create an event, see
 * it in the event tab and add participants to it.
 */
class EndToEndTest : TestCase() {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @After
  fun tearDown() {
    // clean up the event
    runBlocking { eventVM.getAllEvents()?.forEach { eventVM.removeEvent(it.eventID) } }

    // clean up the users
    runBlocking {
      Firebase.auth.currentUser?.delete()
      userVM.deleteUser(uid1)
      userVM.deleteUser(uid2)
    }

    runBlocking {
      val result = Firebase.auth.signInWithEmailAndPassword(email1, pwd1)
      while (!result.isComplete) {
        TimeUnit.SECONDS.sleep(1)
      }
      Firebase.auth.currentUser?.delete()
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
        composeTestRule.onNodeWithText("Email").assertIsDisplayed().performTextInput(email1)
        composeTestRule.onNodeWithText("Password").assertIsDisplayed().performTextInput(pwd1)
        composeTestRule.onNodeWithText("Log in").assertIsEnabled().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil(timeoutMillis = 10000) {
          composeTestRule.onNodeWithTag("CreateUI").isDisplayed()
        }
      }
    }

    ComposeScreen.onComposeScreen<CreateScreen>(composeTestRule) {
      step("Select which type of event to create") {
        composeTestRule.onNodeWithText("Private").assertIsDisplayed().performClick()
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
      composeTestRule.onAllNodesWithTag("EventWidget")[0].assertIsDisplayed().performClick()
      composeTestRule.waitForIdle()
    }

    ComposeScreen.onComposeScreen<EventInfoScreen>(composeTestRule) {
      composeTestRule.waitUntil(timeoutMillis = 10000) {
        composeTestRule.onNodeWithTag("EventHeader").isDisplayed()
      }

      composeTestRule
          .onNodeWithContentDescription("Add to Favorites")
          .assertIsDisplayed()
          .performClick()
      composeTestRule.waitForIdle()
      composeTestRule.onNodeWithContentDescription("Remove from favorites").performClick()
      composeTestRule.onNodeWithTag("EventButton").assertIsDisplayed().performClick().performClick()
      composeTestRule.waitForIdle()
    }
  }

  companion object {

    private const val email1 = "user1@endtoendtest.com"
    private const val pwd1 = "123456"
    private var uid1 = ""
    private const val username1 = "null"

    private const val email2 = "user2@endtoendtest.com"
    private const val pwd2 = "123456"
    private var uid2 = ""
    private const val username2 = "null2"

    private val userVM = UserViewModel(UserRepository(Firebase.firestore))
    private lateinit var eventVM: EventViewModel

    @JvmStatic
    @BeforeClass
    fun setup() {
      TimeUnit.SECONDS.sleep(3)
      runBlocking {
        // create two new users
        var result = Firebase.auth.createUserWithEmailAndPassword(email1, pwd1)
        while (!result.isComplete) {
          TimeUnit.SECONDS.sleep(1)
        }
        uid1 = result.result.user!!.uid

        result = Firebase.auth.createUserWithEmailAndPassword(email2, pwd2)
        while (!result.isComplete) {
          TimeUnit.SECONDS.sleep(1)
        }
        uid2 = result.result.user!!.uid

        userVM.createUserIfNew(
            uid1,
            username1,
            "testfirstname",
            "testlastname",
            email1,
            "testphonenumber",
            "testcountry")
        TimeUnit.SECONDS.sleep(3)
        userVM.createUserIfNew(
            uid2,
            username2,
            "testfirstname",
            "testlastname",
            email2,
            "testphonenumber",
            "testcountry")
        TimeUnit.SECONDS.sleep(3)

        result = Firebase.auth.signInWithEmailAndPassword(email1, pwd1)
        while (!result.isComplete) {
          TimeUnit.SECONDS.sleep(1)
        }
        userVM.follow(uid2)
        eventVM = EventViewModel(uid1, EventRepository(Firebase.firestore))
      }

      TimeUnit.SECONDS.sleep(3)
    }
  }
}
