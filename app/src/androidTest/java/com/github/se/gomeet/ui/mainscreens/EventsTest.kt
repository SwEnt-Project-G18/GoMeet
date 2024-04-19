package com.github.se.gomeet.ui.mainscreens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventsTest {

  @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

  val maxEventsDisplayedOnScreen = 5

  @Test
  fun uiElementsDisplayed() {
    lateinit var navController: NavHostController

    rule.setContent {
      navController = rememberNavController()
      Events(NavigationActions(navController))
    }

    rule.onNodeWithTag("EventsTitle").assertIsDisplayed()
    rule.onNodeWithTag("MyTicketsButton").assertIsDisplayed()
    rule.onNodeWithTag("FavouritesButton").assertIsDisplayed()
    rule.onNodeWithTag("MyEventsButton").assertIsDisplayed()
    rule.onNodeWithTag("MyTicketsText").assertIsDisplayed()
    rule.onNodeWithTag("FavouritesText").assertExists()
    rule.onNodeWithTag("MyEventsText").assertExists()

    rule.onAllNodesWithTag("Card").apply {
      fetchSemanticsNodes().forEachIndexed { i, _ ->
        if (i < maxEventsDisplayedOnScreen) get(i).assertIsDisplayed() else get(i).assertExists()
      }
    }
    rule.onAllNodesWithTag("EventName").apply {
      fetchSemanticsNodes().forEachIndexed { i, _ ->
        if (i < maxEventsDisplayedOnScreen) get(i).assertIsDisplayed() else get(i).assertExists()
      }
    }
    rule.onAllNodesWithTag("UserName").apply {
      fetchSemanticsNodes().forEachIndexed { i, _ ->
        if (i < maxEventsDisplayedOnScreen) get(i).assertIsDisplayed() else get(i).assertExists()
      }
    }
    rule.onAllNodesWithTag("EventDate").apply {
      fetchSemanticsNodes().forEachIndexed { i, _ ->
        if (i < maxEventsDisplayedOnScreen) get(i).assertIsDisplayed() else get(i).assertExists()
      }
    }
    rule.onAllNodesWithTag("EventPicture").apply {
      fetchSemanticsNodes().forEachIndexed { i, _ ->
        if (i < maxEventsDisplayedOnScreen) get(i).assertIsDisplayed() else get(i).assertExists()
      }
    }
  }
}
