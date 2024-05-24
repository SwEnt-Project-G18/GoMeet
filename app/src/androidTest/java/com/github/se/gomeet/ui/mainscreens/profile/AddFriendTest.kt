package com.github.se.gomeet.ui.mainscreens.profile

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddFriendTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  companion object {
    private lateinit var uid1: String
    private const val email1 = "addfriend@test1.com"
    private const val pwd1 = "123456"

    private const val uid2 = "uid2"
    private const val email2 = "addfriend@test2.com"
    private const val firstName2 = "q"
    private const val lastName2 = "w"
    private const val username2 = "qwe"

    private val userVM = UserViewModel()

    @BeforeClass
    @JvmStatic
    fun setup() {
      runBlocking {
        // Create user1
        var result = Firebase.auth.createUserWithEmailAndPassword(email1, pwd1)
        while (!result.isComplete) {
          TimeUnit.SECONDS.sleep(1)
        }
        uid1 = result.result.user!!.uid

        // Add the users to the view model
        userVM.createUserIfNew(
            uid1, "username1", "firstName1", "lastName1", email1, "testphonenumber", "testcountry")
        while (userVM.getUser(uid1) == null) {
          TimeUnit.SECONDS.sleep(1)
        }
        userVM.createUserIfNew(
            uid2, username2, firstName2, lastName2, email2, "testphonenumber", "testcountry")
        while (userVM.getUser(uid2) == null) {
          TimeUnit.SECONDS.sleep(1)
        }

        // Sign in with user1
        result = Firebase.auth.signInWithEmailAndPassword(email1, pwd1)
        while (!result.isComplete) {
          TimeUnit.SECONDS.sleep(1)
        }
      }
    }

    @AfterClass
    @JvmStatic
    fun tearDown() {
      runBlocking {
        // Clean up the users
        Firebase.auth.currentUser?.delete()
        userVM.deleteUser(uid1)
        userVM.deleteUser(uid2)
      }
    }
  }

  @Test
  fun testAddFriend() {
    composeTestRule.setContent { AddFriend(NavigationActions(rememberNavController()), userVM) }

    composeTestRule.waitForIdle()

    composeTestRule.waitUntil(timeoutMillis = 10000) {
      composeTestRule.onAllNodesWithTag("UserItem")[0].isDisplayed()
    }

    composeTestRule.onNodeWithText("Find User").assertIsDisplayed()
    composeTestRule.onNodeWithText("Search").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("UserItem")[0].assertIsDisplayed().assertHasClickAction()
    composeTestRule.onAllNodesWithContentDescription("Profile picture")[0].assertIsDisplayed()
    composeTestRule.onNodeWithText(firstName2, substring = true).assertIsDisplayed()
    composeTestRule.onNodeWithText(username2).assertIsDisplayed()
    composeTestRule.onAllNodesWithText("Follow")[0].assertIsDisplayed().performClick()
  }
}
