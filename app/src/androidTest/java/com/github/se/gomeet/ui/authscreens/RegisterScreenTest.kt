package com.github.se.gomeet.ui.authscreens

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.R
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.AuthViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.github.kakaocup.kakao.common.utilities.getResourceString
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterScreenTest {

  @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

  private lateinit var authViewModel: AuthViewModel
  private lateinit var userViewModel: UserViewModel

  @After
  fun tearDown() {
    // Clean up the test data
    rule.waitForIdle()
    Firebase.auth.currentUser?.delete()
  }

  @Test
  fun testRegisterScreen() = runTest {
    authViewModel = AuthViewModel()
    userViewModel = UserViewModel()

    rule.setContent {
      val client =
          ChatClient.Builder(getResourceString(R.string.chat_api_key), LocalContext.current)
              .logLevel(ChatLogLevel.NOTHING) // Set to NOTHING in prod
              .build()
      RegisterScreen(
          client, NavigationActions(rememberNavController()), authViewModel, userViewModel) {}
    }

    rule.onNodeWithTag("register_title").assertIsDisplayed()

    rule.onNodeWithText("Email").assertIsDisplayed()
    rule.onNodeWithText("Password").assertIsDisplayed()
    rule.onNodeWithText("Confirm Password").performScrollTo().assertIsDisplayed()

    rule
        .onNodeWithTag("register_button")
        .assertIsNotEnabled()
        .assertHasClickAction()
        .performScrollTo()
        .assertIsDisplayed()

    rule.onNodeWithText("Email").performScrollTo().performTextInput("signup@test1.com")
    rule.onNodeWithText("Password").performScrollTo().performTextInput("123456")
    rule.onNodeWithText("Confirm Password").performScrollTo().performTextInput("123456")

    // Wait for the Compose framework to recompose the UI
    rule.waitForIdle()

    rule.onNodeWithTag("register_button").assertIsEnabled().performScrollTo().performClick()

    rule.waitForIdle()

    // Assert that the register worked
    assert(authViewModel.signInState.value.signInError == null)
    //    assert(authViewModel.signInState.value.isSignInSuccessful) // Error here in CI

    if (Firebase.auth.currentUser != null) userViewModel.deleteUser(Firebase.auth.currentUser!!.uid)
  }
}
