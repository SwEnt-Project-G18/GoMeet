package com.github.se.gomeet.ui

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
import com.github.se.gomeet.ui.authscreens.RegisterScreen
import com.github.se.gomeet.viewmodel.AuthViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterScreenTest {

  @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

  @After
  fun tearDown() {
    // Clean up the test data
    // FirebaseAuth.getInstance().currentUser?.delete()
  }

  @Test
  fun testRegisterScreen() = runTest {

    //        val repo: AuthRepository = mock()
    //        whenever(repo.signUpWithEmailPassword(any(), any(), {
    //
    //        }))
    //        val authViewModel: AuthViewModel = AuthViewModel(repo)

    val authViewModel = AuthViewModel()
    val userViewModel = UserViewModel()

    rule.setContent { RegisterScreen(authViewModel, userViewModel) {} }

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

    assert(authViewModel.signInState.value.signInError == null)

    if (authViewModel.signInState.value.isSignInSuccessful) {
      assert(userViewModel.getUser(Firebase.auth.currentUser!!.uid) != null)
    }

    // Assert that the register worked
    //        assert(authViewModel.signInState.value.isSignInSuccessful)

  }
}
