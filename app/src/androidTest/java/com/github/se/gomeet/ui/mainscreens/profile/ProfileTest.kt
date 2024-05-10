package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.model.repository.EventRepository
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
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
          UserViewModel(UserRepository(Firebase.firestore)),
          EventViewModel("1234", EventRepository(Firebase.firestore)))
    }

    TimeUnit.SECONDS.sleep(3)

    composeTestRule.onNodeWithText("My Profile").assertIsDisplayed()

    composeTestRule.onNodeWithContentDescription("image description").assertIsDisplayed()

    composeTestRule.onNodeWithText("Edit Profile").assertIsDisplayed()

    composeTestRule.onNodeWithText("Share Profile").assertIsDisplayed()
  }

  companion object {
    private val userVM = UserViewModel(UserRepository(Firebase.firestore))
    private lateinit var currentUserId: String

    private val usr = "u@t.com"
    private val pwd = "123456"

    @BeforeClass
    @JvmStatic
    fun setUp() {
      TimeUnit.SECONDS.sleep(3)

      // Create a new user and sign in
      var result = Firebase.auth.createUserWithEmailAndPassword(usr, pwd)
      while (!result.isComplete) {
        TimeUnit.SECONDS.sleep(1)
      }
      result = Firebase.auth.signInWithEmailAndPassword(usr, pwd)
      while (!result.isComplete) {
        TimeUnit.SECONDS.sleep(1)
      }

      // Set up the user view model
      // Order is important here, since createUserIfNew sets current user to created user (so we
      // need to create the current user last)
      currentUserId = Firebase.auth.currentUser!!.uid
      userVM.createUserIfNew(currentUserId, "a", "b", "c", usr, "4567", "Angola")
      TimeUnit.SECONDS.sleep(3)
    }

    @AfterClass
    @JvmStatic
    fun tearDown() {
      // Clean up the user view model
      Firebase.auth.currentUser!!.delete()
      userVM.deleteUser(currentUserId)
    }
  }
}
