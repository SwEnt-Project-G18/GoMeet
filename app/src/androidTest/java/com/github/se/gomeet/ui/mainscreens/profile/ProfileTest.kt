package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.model.repository.EventRepository
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
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
        var result = Firebase.auth.createUserWithEmailAndPassword(usr, pwd)
        while (!result.isComplete) {
          TimeUnit.SECONDS.sleep(1)
        }
        uid = result.result.user!!.uid

        // Add the user to the view model
        userVM.createUserIfNew(uid, "a", "b", "c", usr, "4567", "Angola")
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
  fun testProfile() {
    composeTestRule.setContent {
      Profile(
          NavigationActions(rememberNavController()),
          userId = uid,
          UserViewModel(UserRepository(Firebase.firestore)),
          EventViewModel(uid, EventRepository(Firebase.firestore)))
    }

    // Wait for the page to load
    composeTestRule.waitUntil { composeTestRule.onNodeWithTag("Profile Picture").isDisplayed() }

    // Verify that the ui is correctly displayed
    composeTestRule.onNodeWithTag("Profile Picture").assertIsDisplayed()
    composeTestRule.onNodeWithTag("TopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("UserInfo").assertIsDisplayed()
    composeTestRule.onNodeWithText("Edit Profile").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onNodeWithText("Share Profile").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onAllNodesWithTag("EventsListHeader").assertCountEquals(2)
  }
}
