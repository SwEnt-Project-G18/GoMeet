package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

class ProfileTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun profileUiTest() {
    composeTestRule.setContent {
      Profile(
          NavigationActions(rememberNavController()),
          userId = currentUserId,
          UserViewModel(),
          EventViewModel())
    }

    composeTestRule.onNodeWithText("My Profile").assertIsDisplayed()

    composeTestRule.onNodeWithContentDescription("image description").assertIsDisplayed()

    composeTestRule.onNodeWithText("Edit Profile").assertIsDisplayed()

    composeTestRule.onNodeWithText("Share Profile").assertIsDisplayed()
  }

  companion object {
    private val userViewModel = UserViewModel()
    private lateinit var currentUserId: String

    private val usr = "u@t.com"
    private val pwd = "123456"

    @BeforeClass
    @JvmStatic
    fun setUp() {
      Firebase.auth.createUserWithEmailAndPassword(usr, pwd)
      TimeUnit.SECONDS.sleep(2)
      Firebase.auth.signInWithEmailAndPassword(usr, pwd)
      TimeUnit.SECONDS.sleep(2)
      // Set up the user view model
      // Order is important here, since createUserIfNew sets current user to created user (so we
      // need to create the current user last)
      currentUserId = Firebase.auth.currentUser!!.uid
      userViewModel.createUserIfNew(currentUserId, "a", "b", "c", usr, "4567", "Angola")
      TimeUnit.SECONDS.sleep(2)
    }

    @AfterClass
    @JvmStatic
    fun tearDown() {
      // Clean up the user view model
      Firebase.auth.currentUser!!.delete()
      userViewModel.deleteUser(currentUserId)
    }
  }
}
