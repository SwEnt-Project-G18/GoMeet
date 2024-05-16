package com.github.se.gomeet.ui.mainscreens.create

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.model.repository.EventRepository
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
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

        @AfterClass
        @JvmStatic
        fun tearDown() {
            runBlocking {
                // Clean up the event
                eventVM.getAllEvents()?.forEach { eventVM.removeEvent(it.eventID) }
            }
        }
    }

    @Test
    fun testCratePrivateEvent() {
        val eventVM = EventViewModel(uid)

        composeTestRule.setContent {
            CreateEvent(NavigationActions(rememberNavController()), eventVM, isPrivate = true)
        }

        composeTestRule.waitForIdle()

        // Verify that the text fields are displayed and fill them in
        composeTestRule.onNodeWithText("Title").assertIsDisplayed().performTextInput("Sample Event 1")
        composeTestRule.onNodeWithText("Location").assertIsDisplayed().performTextInput("test")
        composeTestRule.onNodeWithTag("DropdownMenu").assertIsDisplayed().performClick()
        composeTestRule
            .onNodeWithText("Date")
            .assertIsDisplayed()
            .performTextInput(LocalDate.now().toString())
        composeTestRule.onNodeWithText("Price").assertIsDisplayed().performTextInput("25.00")
        composeTestRule
            .onNodeWithText("Link")
            .assertIsDisplayed()
            .performTextInput("http://example.com")

        // Add tags
        composeTestRule.onNodeWithText("Add Tags").assertIsDisplayed()
        composeTestRule.onNodeWithTag("TagsButton").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithTag("TagList").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText("Save").assertIsDisplayed().performClick()

        // Verify that the rest of the buttons are displayed and create the event
        composeTestRule.onNodeWithText("Add Participants").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add Image").assertIsDisplayed()
        composeTestRule.onNodeWithText("Post").assertIsDisplayed().performClick()
    }

    @Test
    fun testCreatePublicEvent() {
        composeTestRule.setContent {
            CreateEvent(NavigationActions(rememberNavController()), eventVM, isPrivate = false)
        }

        composeTestRule.waitForIdle()

        // Verify that the ui is correctly displayed
        composeTestRule.onNodeWithText("Title").performTextInput("Sample Event 2")
        composeTestRule.onNodeWithText("Description").performTextInput("This is a test event.")
        composeTestRule.onNodeWithText("Location").performTextInput("test")
        composeTestRule.onNodeWithTag("DropdownMenu").assertIsDisplayed()
        composeTestRule.onNodeWithText("Date").performTextInput("invalid date")
        composeTestRule.onNodeWithText("Price").performTextInput("25.00")
        composeTestRule.onNodeWithText("Link").performTextInput("http://example.com")
        composeTestRule.onNodeWithText("Add Tags").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add Image").assertIsDisplayed()
        composeTestRule.onNodeWithText("Post").assertIsDisplayed()
    }
}