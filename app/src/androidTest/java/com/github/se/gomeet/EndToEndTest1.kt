package com.github.se.gomeet

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.screens.LoginScreen
import com.github.se.gomeet.screens.WelcomeScreen
import com.github.se.gomeet.ui.authscreens.WelcomeScreen
import com.github.se.gomeet.ui.navigation.LOGIN_ITEMS
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EndToEndTest1 : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @Before
  fun testSetup() {
    composeTestRule.setContent {
      WelcomeScreen(
          onNavToLogin = { mockNavActions.navigateTo(LOGIN_ITEMS[1]) },
          onNavToRegister = { mockNavActions.navigateTo(LOGIN_ITEMS[2]) },
          onSignInSuccess = { userId ->
            mockNavActions.navigateTo(TOP_LEVEL_DESTINATIONS[1], clearBackStack = true)
          })
    }
  }

  @Test
  fun test() = run {
    ComposeScreen.onComposeScreen<WelcomeScreen>(composeTestRule) {
      step("Click on log in button") {
        logInButton {
          assertIsDisplayed()
          performClick()
          verify { mockNavActions.navigateTo(LOGIN_ITEMS[1]) }
          confirmVerified(mockNavActions)
        }
      }
    }
    // how to change screen ???
    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
      step("Log in with email and password") {
        logInButton {
          assertIsDisplayed()
          assertIsNotEnabled()
        }
        emailField {
          assertIsDisplayed()
          performTextInput("test@test.com")
        }
        passwordField {
          assertIsDisplayed()
          performTextInput("123456")
        }
        logInButton {
          assertIsEnabled()
          performClick()
          verify { mockNavActions.navigateTo(TOP_LEVEL_DESTINATIONS[1]) }
          confirmVerified(mockNavActions)
        }
      }
    }
  }
}
