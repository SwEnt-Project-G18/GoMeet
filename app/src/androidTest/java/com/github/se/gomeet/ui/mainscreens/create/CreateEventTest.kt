package com.github.se.gomeet.ui.mainscreens.create

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.model.repository.EventRepository
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventViewModel
import com.google.android.gms.maps.MapsInitializer
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

class CreateEventTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @After
  fun tearDown() {
    runBlocking {
      eventViewModel.getAllEvents()?.forEach { eventViewModel.removeEvent(it.eventID) }
    }
  }

  @Test
  fun testCreatePrivateEvent() {
    lateinit var navController: NavHostController

    composeTestRule.setContent {
      MapsInitializer.initialize(LocalContext.current)
      CreateEvent(NavigationActions(rememberNavController()), eventViewModel, isPrivate = true)
    }

    composeTestRule.waitForIdle()

    // Enter text into the Title field
    composeTestRule.onNodeWithText("Title").performTextInput("Sample Event 1")

    // Enter text into the Location field
    composeTestRule.onNodeWithText("Location").performTextInput("test")

    composeTestRule.onNodeWithTag("DropdownMenu").assertIsDisplayed()

    // Enter date
    composeTestRule.onNodeWithText("Date").performTextInput(LocalDate.now().toString())
    // Enter a price
    composeTestRule.onNodeWithText("Price").performTextInput("25.00")
    // Enter a URL
    composeTestRule.onNodeWithText("Link").performTextInput("http://example.com")

    composeTestRule.onNodeWithText("Add Tags").assertIsDisplayed()

    composeTestRule.onNodeWithText("Add Participants").assertIsDisplayed()

    composeTestRule.onNodeWithText("Add Image").assertIsDisplayed().performClick()

    composeTestRule.onNodeWithText("Post").performClick()
  }

  @Test
  fun testCreatePublicEvent() {
    lateinit var navController: NavHostController

    composeTestRule.setContent {
      navController = rememberNavController()
      CreateEvent(NavigationActions(navController), eventViewModel, isPrivate = false)
    }

    // Enter text into the Title field
    composeTestRule.onNodeWithText("Title").performTextInput("Sample Event 2")

    // Enter text into the Description field
    composeTestRule.onNodeWithText("Description").performTextInput("This is a test event.")

    // Enter text into the Location field
    composeTestRule.onNodeWithText("Location").performTextInput("test")

    composeTestRule.onNodeWithTag("DropdownMenu").assertIsDisplayed()

    // Enter date
    composeTestRule.onNodeWithText("Date").performTextInput("invalid date")
    // Enter a price
    composeTestRule.onNodeWithText("Price").performTextInput("25.00")
    // Enter a URL
    composeTestRule.onNodeWithText("Link").performTextInput("http://example.com")

    composeTestRule.onNodeWithText("Add Tags").assertIsDisplayed()
    composeTestRule.onNodeWithTag("TagsButton").performClick()
    composeTestRule.onNodeWithTag("TagList").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText("Save").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText("Edit Tags").assertIsDisplayed()

    composeTestRule.onNodeWithText("Add Image").assertIsDisplayed()

    composeTestRule.onNodeWithText("Post").performClick()
  }

  companion object {

    private lateinit var eventViewModel: EventViewModel

    @JvmStatic
    @BeforeClass
    fun setup() {
      eventViewModel = EventViewModel("null", EventRepository(Firebase.firestore))
    }
  }
}
