package com.github.se.gomeet.endtoend

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.se.gomeet.MainActivity
import com.github.se.gomeet.screens.CreateEventScreen
import com.github.se.gomeet.screens.CreateScreen
import com.github.se.gomeet.screens.EventsScreen
import com.github.se.gomeet.screens.ExploreScreen
import com.github.se.gomeet.screens.LoginScreenScreen
import com.github.se.gomeet.screens.WelcomeScreenScreen
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.viewmodel.AuthViewModel
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
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
  @get:Rule
  var permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

  companion object {
    private const val email = "user@test.com"
    private const val pwd = "123456"
    private lateinit var uid: String
    private const val username = "EndToEndTestuser"

    private lateinit var userVM: UserViewModel
    private lateinit var eventVM: EventViewModel
    private val authViewModel = AuthViewModel()

    @JvmStatic
    @BeforeClass
    fun setup() = runBlocking {
      // create a new user
      Firebase.auth.createUserWithEmailAndPassword(email, pwd).await()
      uid = Firebase.auth.currentUser!!.uid

      userVM = UserViewModel(uid)

      // Add the user to the view model
      userVM.createUserIfNew(
          uid, username, "testfirstname", "testlastname", email, "testphonenumber", "testcountry")
      while (userVM.getUser(uid) == null) {
        TimeUnit.SECONDS.sleep(1)
      }

      // Sign in
      Firebase.auth.signInWithEmailAndPassword(email, pwd).await()
      eventVM = EventViewModel(uid)
      authViewModel.signOut()

      TimeUnit.SECONDS.sleep(1)
    }

    @AfterClass
    @JvmStatic
    fun tearDown() = runBlocking {

      // Clean up the event
      eventVM.getAllEvents()?.forEach { eventVM.removeEvent(it.eventID) }
      // Clean up the user
      Firebase.auth.currentUser?.delete()?.await()
      userVM.deleteUser(uid)

      return@runBlocking
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
        composeTestRule.onNodeWithText("Log In").assertIsDisplayed().assertIsNotEnabled()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed().performTextInput(email)
        composeTestRule.onNodeWithText("Password").assertIsDisplayed().performTextInput(pwd)
        composeTestRule.onNodeWithText("Log In").assertIsEnabled().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil(timeoutMillis = 10000) {
          composeTestRule.onNodeWithTag("ExploreUI").isDisplayed()
        }
      }
    }

    ComposeScreen.onComposeScreen<ExploreScreen>(composeTestRule) {
      step("Go to Events") {
        composeTestRule.onNodeWithTag(Route.EVENTS).assertIsDisplayed().performClick()
      }
    }

    composeTestRule.waitForIdle()

    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      composeTestRule.onNodeWithTag("CreateEventButton").assertIsDisplayed().performClick()
    }

    composeTestRule.waitForIdle()

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
        runBlocking {
          while (userVM.getUser(uid)!!.myEvents.isEmpty()) {
            TimeUnit.SECONDS.sleep(1)
          }
        }
      }
    }
    composeTestRule.onNodeWithTag(Route.EVENTS).performClick()
    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      composeTestRule.waitForIdle()
      composeTestRule.onNodeWithTag("Events").performClick()
      composeTestRule.waitUntil(timeoutMillis = 10000) {
        composeTestRule.onAllNodesWithTag("Card")[0].isDisplayed()
      }
      composeTestRule.onAllNodesWithTag("Card")[0].assertIsDisplayed()
    }
  }
}
