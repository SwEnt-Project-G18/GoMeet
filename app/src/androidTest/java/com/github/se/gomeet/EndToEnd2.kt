package com.github.se.gomeet

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.screens.CreateEventScreen
import com.github.se.gomeet.screens.CreateScreen
import com.github.se.gomeet.screens.LoginScreen
import com.github.se.gomeet.screens.TrendsScreen
import com.github.se.gomeet.screens.WelcomeScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.junit4.MockKRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EndToEndTest2 : TestCase() {

    @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule val mockkRule = MockKRule(this)

    @Test
    fun test() = run {
        ComposeScreen.onComposeScreen<WelcomeScreen>(composeTestRule) {
            step("Click on log in button") {
                logInButton {
                    assertIsDisplayed()
                    performClick()
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
                }
            }
        }


        // First ensure login and switch to the expected screen
        ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
            step("Log in with email and password") {
                logInButton {
                    performClick()
                }
            }
        }

        composeTestRule.waitUntil(timeoutMillis = 10000) {
            try {
                ComposeScreen.onComposeScreen<CreateScreen>(composeTestRule) {
                    createPublicEventButton {
                        isDisplayed()
                    }
                }
                true // Return true if the button is displayed
            } catch (e: AssertionError) {
                false // Return false if the assertion fails
            }
        }

        ComposeScreen.onComposeScreen<CreateScreen>(composeTestRule) {
            step("goTo publicCreate") {
                createPublicEventButton {
                    assertIsDisplayed()
                    performClick()
                }
            }
        }



        ComposeScreen.onComposeScreen<CreateEventScreen>(composeTestRule) {
            step("add event"){
                title{
                    assertIsDisplayed()
                    performTextInput("Title")
                }
                description{
                    assertIsDisplayed()
                    performTextInput("Description")
                }
                location{
                    assertIsDisplayed()
                    performTextInput("Lausanne")
                }
                date{
                    assertIsDisplayed()
                    performTextInput("2003-01-01")
                }
                price{
                    assertIsDisplayed()
                    performTextInput("0.0")
                }
                link{
                    assertIsDisplayed()
                    performTextInput("https://example.com")
                }
                postButton{
                    assertIsDisplayed()
                    performClick()
                }

            }
        }

    }
}
