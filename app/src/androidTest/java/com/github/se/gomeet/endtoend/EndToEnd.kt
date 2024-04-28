package com.github.se.gomeet.endtoend

import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.MainActivity
import com.github.se.gomeet.screens.CreateEventScreen
import com.github.se.gomeet.screens.CreateScreen
import com.github.se.gomeet.screens.LoginScreen
import com.github.se.gomeet.screens.WelcomeScreenScreen
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class EndToEndTest : TestCase() {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @After
  fun tearDown() {

    runBlocking {
      eventVM.getAllEvents()?.forEach {
        if (it.creator == uid)
          eventVM.removeEvent(it.uid)
      }
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
        }
      }
    }

    composeTestRule.waitForIdle()

    ComposeScreen.onComposeScreen<CreateScreen>(composeTestRule) {
      step("goTo publicCreate") {
        createPublicEventButton {
          assertIsDisplayed()
          performClick()
        }
      }
    }

    composeTestRule.waitForIdle()

    ComposeScreen.onComposeScreen<CreateEventScreen>(composeTestRule) {
      step("add event") {
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
          performTextInput("Lausanne")
        }
        date {
          assertIsDisplayed()
          performTextInput("2003-01-01")
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

    private val email = "qwe@asd.com"
    private val pwd = "123456"
    private val uid = "testuid"
    private val username = "testuser"

    private lateinit var userVM: UserViewModel
    private lateinit var eventVM: EventViewModel
    @JvmStatic
    @BeforeClass
    fun setup() {
      Firebase.auth.useEmulator("10.0.2.2", 9099)
      Firebase.firestore.useEmulator("10.0.2.2", 8080)
      Firebase.storage.useEmulator("10.0.2.2", 9199)

      userVM = UserViewModel()
      userVM.createUserIfNew(uid, username)
      Firebase.auth.createUserWithEmailAndPassword(email, pwd)
      TimeUnit.SECONDS.sleep(2)

      eventVM = EventViewModel(uid)
    }
  }
}


