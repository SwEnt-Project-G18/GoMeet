package com.github.se.gomeet.ui.mainscreens.create

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventViewModel
import java.time.LocalDate
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateEventTest {

  @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun testCreateEventScreen_InputFields() {
    lateinit var navController: NavHostController
    val eventViewModel = EventViewModel()

    rule.setContent {
      navController = rememberNavController()
      CreateEvent(NavigationActions(navController), eventViewModel, isPrivate = true)
    }

    // Enter text into the Title field
    rule.onNodeWithText("Title").performTextInput("Sample Event")

    // Enter text into the Description field
    rule.onNodeWithText("Description").performTextInput("This is a test event.")

    // Enter text into the Location field
    rule.onNodeWithText("Location").performTextInput("123 Test Street")

    // Enter date
    rule.onNodeWithText("Date").performTextInput(LocalDate.now().toString())
    // Enter a price
    rule.onNodeWithText("Price").performTextInput("25.00")
    // Enter a URL
    rule.onNodeWithText("Link").performTextInput("http://example.com")

    rule.onNodeWithText("Post").performClick()
  }

  @Test
  fun testPublicCreateEventScreen_InputFields() {
    lateinit var navController: NavHostController
    val eventViewModel = EventViewModel()

    rule.setContent {
      navController = rememberNavController()
      CreateEvent(NavigationActions(navController), eventViewModel, isPrivate = false)
    }

    // Enter text into the Title field
    rule.onNodeWithText("Title").performTextInput("Sample Event")

    // Enter text into the Description field
    rule.onNodeWithText("Description").performTextInput("This is a test event.")

    // Enter text into the Location field
    rule.onNodeWithText("Location").performTextInput("123 Test Street")

    // Enter date
    rule.onNodeWithText("Date").performTextInput(LocalDate.now().toString())
    // Enter a price
    rule.onNodeWithText("Price").performTextInput("25.00")
    // Enter a URL
    rule.onNodeWithText("Link").performTextInput("http://example.com")

    rule.onNodeWithText("Post").performClick()
  }
}
