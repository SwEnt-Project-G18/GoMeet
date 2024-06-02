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
import com.github.se.gomeet.R
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventCreationViewModel
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import io.github.kakaocup.kakao.common.utilities.getResourceString
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
      eventVM.getAllEvents { events -> events?.forEach { eventVM.removeEvent(it.eventID) } }
    }
  }

  @Test
  fun testCreatePrivateEvent() {
    val eventVM = EventViewModel(uid)

    composeTestRule.setContent {
      CreateEvent(
          NavigationActions(rememberNavController()),
          eventVM,
          isPrivate = true,
          userVM,
          eventCreationVM)
    }

    composeTestRule.waitForIdle()

    // Verify that the text fields are displayed and fill them in
    composeTestRule
        .onNodeWithText(getResourceString(R.string.title_text_field))
        .assertIsDisplayed()
        .performTextInput("Sample Event 1")
    composeTestRule
        .onNodeWithText(getResourceString(R.string.location_text_field))
        .assertIsDisplayed()
        .performTextInput("test")
    composeTestRule.onNodeWithTag("DropdownMenu").assertIsDisplayed().performClick()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.pick_date))
        .assertIsDisplayed()
        .performClick()
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.date_picker_ok))
        .assertIsDisplayed()
        .performClick()
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.pick_time))
        .assertIsDisplayed()
        .performClick()
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.date_picker_ok))
        .assertIsDisplayed()
        .performClick()
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.price_text_field))
        .assertIsDisplayed()
        .performScrollTo()
        .performTextInput("25.00")
    composeTestRule
        .onNodeWithText(getResourceString(R.string.link_text_field))
        .performScrollTo()
        .assertIsDisplayed()
        .performTextInput("http://example.com")

    // Add tags
    composeTestRule
        .onNodeWithText(getResourceString(R.string.add_tags))
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag("TagsButton").performScrollTo().assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("TagList").assertIsDisplayed().performClick()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.save))
        .assertIsDisplayed()
        .performClick()

    // Verify that the rest of the buttons are displayed and create the event
    composeTestRule
        .onNodeWithText(getResourceString(R.string.add_participants))
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.add_image))
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.post_event_button))
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()
  }

  @Test
  fun testCreatePublicEvent() {
    composeTestRule.setContent {
      CreateEvent(
          NavigationActions(rememberNavController()),
          eventVM,
          isPrivate = false,
          userVM,
          eventCreationVM)
    }

    composeTestRule.waitForIdle()

    // Verify that the ui is correctly displayed
    composeTestRule
        .onNodeWithText(getResourceString(R.string.title_text_field))
        .performTextInput("Sample Event 2")
    composeTestRule
        .onNodeWithText(getResourceString(R.string.description_text_field))
        .performTextInput("This is a test event.")
    composeTestRule
        .onNodeWithText(getResourceString(R.string.location_text_field))
        .performTextInput("test")
    composeTestRule.onNodeWithTag("DropdownMenu").assertIsDisplayed()
    composeTestRule.onNodeWithText(getResourceString(R.string.pick_date)).assertIsDisplayed()
    composeTestRule.onNodeWithText(getResourceString(R.string.pick_time)).assertIsDisplayed()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.price_text_field))
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.link_text_field))
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.add_tags))
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.add_image))
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.post_event_button))
        .performScrollTo()
        .assertIsDisplayed()
  }
}
