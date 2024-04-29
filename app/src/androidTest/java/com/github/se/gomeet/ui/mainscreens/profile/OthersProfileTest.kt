package com.github.se.gomeet.ui.mainscreens.profile

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OthersProfileTest {
  @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun uiElementsDisplayed() {
    lateinit var navController: NavHostController

    rule.setContent {
      navController = rememberNavController()
      OthersProfile(NavigationActions(navController), "")
    }

    rule.onNodeWithTag("TopBar").assertIsDisplayed()
    rule.onNodeWithTag("UserInfo").assertIsDisplayed()
    rule.onNodeWithText("Follow").assertIsDisplayed()
    rule.onNodeWithText("Message").assertIsDisplayed()
    rule.onNodeWithText("Tags").assertIsDisplayed()
    rule.onNodeWithTag("MoreUserInfo").assertIsDisplayed()
    rule.onNodeWithTag("TagList").assertIsDisplayed()
    rule.onAllNodesWithTag("EventsListHeader")[0].assertIsDisplayed()
    rule.onAllNodesWithTag("EventsListItems")[0].assertIsDisplayed()
  }
}