package com.github.se.gomeet.ui.authscreens

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.viewmodel.AuthViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

  private val testEmail = "instrumented@test.com"
  private val testPwd = "itest123456"

  @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setup() {
    // Use Firebase Emulator and create user for logging in
    Firebase.auth.useEmulator("10.0.2.2", 9099)
    Firebase.auth.createUserWithEmailAndPassword(testEmail, testPwd)
  }

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

    rule.setContent { LoginScreen(authViewModel) {} }

    // Test the UI elements
    rule.onNodeWithContentDescription("GoMeet").assertIsDisplayed()
    rule.onNodeWithText("Login").assertIsDisplayed()

    rule.onNodeWithText("Email").assertIsDisplayed()
    rule.onNodeWithText("Password").assertIsDisplayed()
    rule.onNodeWithText("Log in").assertIsNotEnabled().assertHasClickAction().assertIsDisplayed()

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
    assert(authViewModel.signInState.value.isSignInSuccessful)
  }
}
