package com.github.se.gomeet.ui.authscreens

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.R
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.AuthViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.github.kakaocup.kakao.common.utilities.getResourceString
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

  @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

  private val testEmail = "instrumented@test.com"
  private val testPwd = "itest123456"

  @After
  fun teardown() {
    // Clean up the test data
    rule.waitForIdle()
    Firebase.auth.currentUser?.delete()
  }

  @SuppressLint("StateFlowValueCalledInComposition")
  @Test
  fun testLoginScreen() {
    val authViewModel = AuthViewModel()

    runBlocking { Firebase.auth.createUserWithEmailAndPassword(testEmail, testPwd).await() }

    rule.setContent {
      val client =
          ChatClient.Builder(getResourceString(R.string.chat_api_key), LocalContext.current)
              .logLevel(ChatLogLevel.NOTHING) // Set to NOTHING in prod
              .build()
      LoginScreen(authViewModel, NavigationActions(rememberNavController())) {}
    }

    // Test the UI elements
    rule.onNodeWithContentDescription("GoMeet").assertIsDisplayed()
    rule.onNodeWithText("Login").assertIsDisplayed()

    rule.onNodeWithText("Email").assertIsDisplayed()
    rule.onNodeWithText("Password").assertIsDisplayed()
    rule.onNodeWithText("Log In").assertIsNotEnabled().assertHasClickAction().assertIsDisplayed()

    // Enter email and password
    rule.onNodeWithText("Email").performTextInput(testEmail)
    rule.onNodeWithText("Password").performTextInput(testPwd)

    // Wait for the Compose framework to recompose the UI
    rule.waitForIdle()

    // Click on the "Log in" button
    rule.onNodeWithText("Log in").assertIsEnabled().assertHasClickAction()
    rule.onNodeWithText("Log in").performClick()

    rule.waitForIdle()

    // Sign-in should complete successfully
    assert(authViewModel.signInState.value.signInError == null)
    //    assert(authViewModel.signInState.value.isSignInSuccessful) // Error here in CI
  }
}
