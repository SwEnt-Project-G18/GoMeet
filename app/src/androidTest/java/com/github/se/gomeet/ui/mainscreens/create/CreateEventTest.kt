package com.github.se.gomeet.ui.mainscreens.create

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventCreationViewModel
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import kotlinx.coroutines.runBlocking
import org.junit.AfterClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateEventTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  companion object {
    private val uid = "CreateEventTestUser"
    private val eventVM = EventViewModel(uid)
    private val userVM = UserViewModel(uid)
    private val eventCreationVM = EventCreationViewModel()

    @AfterClass
    @JvmStatic
    fun tearDown() = runBlocking {

      // Clean up the events
      eventVM.getAllEvents()?.forEach { eventVM.removeEvent(it.eventID) }
      return@runBlocking
    }
  }

  @Test
  fun testCratePrivateEvent() {
    val eventVM = EventViewModel(uid)

    composeTestRule.setContent {
      CreateEvent(NavigationActions(rememberNavController()), eventVM, isPrivate = true, userVM, eventCreationVM)
    }

    composeTestRule.waitForIdle()

    // Verify that the text fields are displayed and fill them in
    composeTestRule.onNodeWithText("Title").assertIsDisplayed().performTextInput("Sample Event 1")
    composeTestRule.onNodeWithText("Location").assertIsDisplayed().performTextInput("test")
    composeTestRule.onNodeWithTag("DropdownMenu").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText("Pick date").assertIsDisplayed().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("OK").assertIsDisplayed().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Pick time").assertIsDisplayed().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("OK").assertIsDisplayed().performClick()
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithText("Price")
        .assertIsDisplayed()
        .performScrollTo()
        .performTextInput("25.00")
    composeTestRule
        .onNodeWithText("Link")
        .performScrollTo()
        .assertIsDisplayed()
        .performTextInput("http://example.com")

    // Add tags
    composeTestRule.onNodeWithText("Add Tags").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("TagsButton").performScrollTo().assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("TagList").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText("Save").assertIsDisplayed().performClick()

    // Verify that the rest of the buttons are displayed and create the event
    composeTestRule.onNodeWithText("Add Participants").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("Add Image").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("Post").performScrollTo().assertIsDisplayed().performClick()
  }

  @Test
  fun testCreatePublicEvent() {
    composeTestRule.setContent {
      CreateEvent(NavigationActions(rememberNavController()), eventVM, isPrivate = false, userVM, eventCreationVM)
    }

    composeTestRule.waitForIdle()

    // Verify that the ui is correctly displayed
    composeTestRule.onNodeWithText("Title").performTextInput("Sample Event 2")
    composeTestRule.onNodeWithText("Description").performTextInput("This is a test event.")
    composeTestRule.onNodeWithText("Location").performTextInput("test")
    composeTestRule.onNodeWithTag("DropdownMenu").assertIsDisplayed()
    composeTestRule.onNodeWithText("Pick date").assertIsDisplayed()
    composeTestRule.onNodeWithText("Pick time").assertIsDisplayed()
    composeTestRule.onNodeWithText("Price").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("Link").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("Add Tags").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("Add Image").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("Post").performScrollTo().assertIsDisplayed()
  }
}
