package com.github.se.gomeet.ui.mainscreens.profile

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QRCodeScannerScreenTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()
  @get:Rule var permissionRule = GrantPermissionRule.grant(android.Manifest.permission.CAMERA)

  @Test
  fun testSettingsAbout() {
    composeTestRule.setContent {
      QRCodeScannerScreen(NavigationActions(rememberNavController()), EventViewModel())
    }

    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithContentDescription("Go back")
        .assertIsDisplayed()
        .assertHasClickAction()
    composeTestRule.onNodeWithTag("CameraView").assertIsDisplayed()
    composeTestRule
        .onNodeWithContentDescription("Upload QR code from gallery")
        .assertIsDisplayed()
        .performClick()
  }
}
