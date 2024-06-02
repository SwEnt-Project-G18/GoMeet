package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.R
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.github.kakaocup.kakao.common.utilities.getResourceString
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileTest {
  @get:Rule val composeTestRule = createComposeRule()

  companion object {
    private lateinit var userVM: UserViewModel
    private lateinit var uid: String

    private val usr = "profile@test.com"
    private val pwd = "123456"

    @BeforeClass
    @JvmStatic
    fun setUp() = runBlocking {
      // Create a new user and sign in
      Firebase.auth.createUserWithEmailAndPassword(usr, pwd).await()
      uid = Firebase.auth.currentUser!!.uid

      userVM = UserViewModel(uid)

      // Add the user to the view model
      userVM.createUserIfNew(uid, "a", "b", "c", usr, "4567", "Angola", "")
      while (userVM.getUser(uid) == null) {
        TimeUnit.SECONDS.sleep(1)
      }
    }

    @AfterClass
    @JvmStatic
    fun tearDown() = runBlocking {
      // Clean up the user
      UserRepository.removeUser(uid)
      Firebase.auth.currentUser!!.delete().await()

      return@runBlocking
    }
  }

  @Test
  fun testProfile() {
    composeTestRule.setContent {
      Profile(NavigationActions(rememberNavController()), userVM, EventViewModel(uid))
    }

    // Wait for the page to load
    composeTestRule.waitUntil { composeTestRule.onNodeWithTag("Profile Picture").isDisplayed() }

    // Verify that the ui is correctly displayed
    composeTestRule.onNodeWithTag("Profile Picture").assertIsDisplayed()
    composeTestRule.onNodeWithTag("TopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("UserInfo").assertIsDisplayed()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.edit_profile_button))
        .assertIsDisplayed()
        .assertHasClickAction()
    composeTestRule.onAllNodesWithTag("EventsListHeader").assertCountEquals(2)
    composeTestRule
        .onNodeWithText(getResourceString(R.string.share_profile))
        .assertIsDisplayed()
        .performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithContentDescription("Save").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onNodeWithContentDescription("Close").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onNodeWithContentDescription("QR Code").assertIsDisplayed()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.share))
        .assertIsDisplayed()
        .performClick()
  }
}
