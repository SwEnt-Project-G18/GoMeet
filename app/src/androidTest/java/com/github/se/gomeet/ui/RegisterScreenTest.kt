package com.github.se.gomeet.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.viewmodel.AuthViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterScreenTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testRegisterScreen() {
        val authViewModel = AuthViewModel()

        lateinit var nav: NavHostController

        rule.setContent {
            nav = rememberNavController()
            RegisterScreen(authViewModel) {
                NavigationActions(nav).navigateTo(TOP_LEVEL_DESTINATIONS[1])
            }
        }

        rule.onNodeWithContentDescription("GoMeet").assertIsDisplayed()
        rule.onNodeWithTag("register_title").assertIsDisplayed()

        rule.onNodeWithText("Email").assertIsDisplayed()
        rule.onNodeWithText("Password").assertIsDisplayed()
        rule.onNodeWithText("Confirm Password").assertIsDisplayed()

        rule.onNodeWithTag("register_button")
            .assertIsNotEnabled().assertHasClickAction().assertIsDisplayed()

        rule.onNodeWithText("Email").performTextInput("signup@test.com")
        rule.onNodeWithText("Password").performTextInput("123456")
        rule.onNodeWithText("Confirm Password").performTextInput("123456")

        // Wait for the Compose framework to recompose the UI
        rule.waitForIdle()

        rule.onNodeWithTag("register_button").assertIsEnabled()
    }
}
