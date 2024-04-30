package com.github.se.gomeet.endtoend

import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.MainActivity
import com.github.se.gomeet.screens.CreateEventScreen
import com.github.se.gomeet.screens.CreateScreen
import com.github.se.gomeet.screens.LoginScreen
import com.github.se.gomeet.screens.WelcomeScreenScreen
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EndToEndTest : TestCase() {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @After
  fun tearDown() {

    runBlocking {
      eventVM.getAllEvents()?.forEach { if (it.creator == uid) eventVM.removeEvent(it.uid) }
    }

    // Clean up the user
    Firebase.auth.currentUser?.delete()
    userVM.deleteUser(uid)
  }

  @Test
  fun test() = run {
    ComposeScreen.onComposeScreen<WelcomeScreenScreen>(composeTestRule) {
      step("Click on log in button") {
        logInButton {
          assertIsDisplayed()
          performClick()
        }
      }
    }

    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
      step("Log in with email and password") {
        logInButton {
          assertIsDisplayed()
          assertIsNotEnabled()
        }
        emailField {
          assertIsDisplayed()
          performTextInput(email)
        }
        passwordField {
          assertIsDisplayed()
          performTextInput(pwd)
        }
        logInButton {
          assertIsEnabled()
          performClick()
          composeTestRule.waitForIdle()
          composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onNodeWithTag("CreateUI").isDisplayed()
          }
        }
      }
    }

    ComposeScreen.onComposeScreen<CreateScreen>(composeTestRule) {
      step("Select which type of event to create") {
        createPublicEventButton {
          assertIsDisplayed()
          performClick()
        }
      }
    }

    composeTestRule.waitForIdle()

    ComposeScreen.onComposeScreen<CreateEventScreen>(composeTestRule) {
      step("Create an event") {
        title {
          assertIsDisplayed()
          performTextInput("Title")
        }
        description {
          assertIsDisplayed()
          performTextInput("Description")
        }
        location {
          assertIsDisplayed()
          performTextInput("t")
          performTextInput("e")
          performTextInput("s")
          performTextInput("t")
          composeTestRule.waitForIdle()
          composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithTag("DropdownMenuItem")[0].isDisplayed()
          }
          composeTestRule.onAllNodesWithTag("DropdownMenuItem")[0].performClick()
        }
        date {
          assertIsDisplayed()
          performTextInput("2024-07-23")
        }
        price {
          assertIsDisplayed()
          performTextInput("0.0")
        }
        link {
          assertIsDisplayed()
          performTextInput("https://example.com")
        }
        postButton {
          assertIsDisplayed()
          performClick()
        }
      }
    }
  }

  companion object {

    private val email = "test@test.com"
    private val pwd = "123456"
    private val uid = "testuid"
    private val username = "testuser"

    private lateinit var userVM: UserViewModel
    private lateinit var eventVM: EventViewModel

    @JvmStatic
    @BeforeClass
    fun setup() {

      userVM = UserViewModel()
      userVM.createUserIfNew(uid, username)
      Firebase.auth.createUserWithEmailAndPassword(email, pwd)
      TimeUnit.SECONDS.sleep(2)

      eventVM = EventViewModel(uid)
    }
  }
}
