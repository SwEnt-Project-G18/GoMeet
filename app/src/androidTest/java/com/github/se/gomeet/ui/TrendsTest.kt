package com.github.se.gomeet.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.ui.mainscreens.Trends
import com.github.se.gomeet.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TrendsTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun uiElementsDisplayed() {
        lateinit var navController: NavHostController

        rule.setContent {
            navController = rememberNavController()
            Trends(NavigationActions(navController))
        }

        rule.onNodeWithText("Trends").assertIsDisplayed()
    }
}
