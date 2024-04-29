package com.github.se.gomeet.ui.authscreens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.viewmodel.AuthViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.getstream.chat.android.client.ChatClient
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterScreenTest {

  @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setup() {
    // Make all subsequent calls to Firebase auth use the emulator
    Firebase.auth.useEmulator("10.0.2.2", 9099)
  }

  @After
  fun tearDown() {
    // Clean up the test data
    rule.waitForIdle()
    Firebase.auth.currentUser?.delete()
  }

  @Test
  fun testRegisterScreen() = runTest {
    val authViewModel = AuthViewModel()
    val userViewModel = UserViewModel()

    rule.setContent { RegisterScreen(ChatClient.instance(), authViewModel, userViewModel) {} }

    rule.onNodeWithTag("register_title").assertIsDisplayed()

    rule.onNodeWithText("Email").assertIsDisplayed()
    rule.onNodeWithText("Password").assertIsDisplayed()
    rule.onNodeWithText("Confirm Password").assertIsDisplayed()

    rule
        .onNodeWithTag("register_button")
        .assertIsNotEnabled()
        .assertHasClickAction()
        .assertIsDisplayed()

    rule.onNodeWithText("Email").performTextInput("signup@test1.com")
    rule.onNodeWithText("Password").performTextInput("123456")
    rule.onNodeWithText("Confirm Password").performTextInput("123456")

    // Wait for the Compose framework to recompose the UI
    rule.waitForIdle()

    rule.onNodeWithTag("register_button").assertIsEnabled().performClick()

    rule.waitForIdle()

    // Assert that the register worked
    assert(authViewModel.signInState.value.signInError == null)
    assert(authViewModel.signInState.value.isSignInSuccessful)
  }
}
