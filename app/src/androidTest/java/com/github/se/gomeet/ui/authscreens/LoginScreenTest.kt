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
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  private val email = "loginscreen@test.com"
  private val pwd = "123456"

  @SuppressLint("StateFlowValueCalledInComposition")
  @Test
  fun testLoginScreen() {
    val authViewModel = AuthViewModel()

    runBlocking { Firebase.auth.createUserWithEmailAndPassword(email, pwd).await() }

    composeTestRule.setContent {
      ChatClient.Builder(getResourceString(R.string.chat_api_key), LocalContext.current)
          .logLevel(ChatLogLevel.NOTHING) // Set to NOTHING in prod
          .build()
      LoginScreen(authViewModel, NavigationActions(rememberNavController())) {}
    }

    composeTestRule.waitForIdle()

    // Test the UI elements
    composeTestRule.onNodeWithContentDescription("GoMeet").assertIsDisplayed()
    composeTestRule.onNodeWithText("Login").assertIsDisplayed()

    composeTestRule.onNodeWithText("Email").assertIsDisplayed()
    composeTestRule.onNodeWithText("Password").assertIsDisplayed()
    composeTestRule
        .onNodeWithText("Log in")
        .assertIsNotEnabled()
        .assertHasClickAction()
        .assertIsDisplayed()

    // Enter email and password
    composeTestRule.onNodeWithText("Email").performTextInput(email)
    composeTestRule.onNodeWithText("Password").performTextInput(pwd)

    // Wait for the Compose framework to recompose the UI
    composeTestRule.waitForIdle()

    // Click on the "Log in" button
    composeTestRule.onNodeWithText("Log in").assertIsEnabled().assertHasClickAction()
    composeTestRule.onNodeWithText("Log in").performClick()

    composeTestRule.waitForIdle()

    // Sign-in should complete successfully
    assert(authViewModel.signInState.value.signInError == null)
  }

  @After
  fun teardown() {
    runBlocking {
      // Clean up the test data
      Firebase.auth.currentUser?.delete()
    }
  }
}
