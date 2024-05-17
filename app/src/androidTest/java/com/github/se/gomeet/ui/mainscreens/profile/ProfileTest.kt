package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import org.junit.Rule
import org.junit.Test

class ProfileTest {

  @get:Rule val composeTestRule = createComposeRule()

  companion object {
    private val userVM = UserViewModel(UserRepository(Firebase.firestore))
    private lateinit var uid: String

    private val usr = "profile@test.com"
    private val pwd = "123456"

    @BeforeClass
    @JvmStatic
    fun setUp() {
      runBlocking {
        // Create a new user and sign in
        val result = Firebase.auth.createUserWithEmailAndPassword(usr, pwd)
        while (!result.isComplete) {
          TimeUnit.SECONDS.sleep(1)
        }
        uid = result.result.user!!.uid

        // Add the user to the view model
        userVM.createUserIfNew(uid, "a", "b", "c", usr, "4567", "Angola", "")
        while (userVM.getUser(uid) == null) {
          TimeUnit.SECONDS.sleep(1)
        }
      }
    }

    @AfterClass
    @JvmStatic
    fun tearDown() {
      // Clean up the user
      runBlocking {
        Firebase.auth.currentUser!!.delete()
        userVM.deleteUser(uid)
      }
    }
  }

  @Test
  fun profileUiTest() {
    composeTestRule.setContent {
      Profile(
          NavigationActions(rememberNavController()),
          userId = "1234",
          UserViewModel(),
          EventViewModel())
    }

    composeTestRule.onNodeWithText("My Profile").assertIsDisplayed()

    composeTestRule.onNodeWithContentDescription("image description").assertIsDisplayed()

    composeTestRule.onNodeWithText("Edit Profile").assertIsDisplayed()

    composeTestRule.onNodeWithText("Share Profile").assertIsDisplayed()
  }
}
