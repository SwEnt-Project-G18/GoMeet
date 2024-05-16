package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.ui.navigation.NavigationActions
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
class EditProfileTest {
  @get:Rule val composeTestRule = createComposeRule()

  companion object {
    private val userVM = UserViewModel(UserRepository(Firebase.firestore))
    private lateinit var uid: String

    private val usr = "editprofile@test.com"
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
        result = Firebase.auth.signInWithEmailAndPassword(usr, pwd)
        while (!result.isComplete) {
          TimeUnit.SECONDS.sleep(1)
        }

        // Add the user to the view model
        uid = Firebase.auth.currentUser!!.uid
        userVM.createUserIfNew(uid, "a", "b", "c", usr, "4567", "Angola")
        while (userVM.getUser(uid) == null) {
          TimeUnit.SECONDS.sleep(1)
        }
      }
    }

    @AfterClass
    @JvmStatic
    fun tearDown() {
      runBlocking {
        // Clean up the user
        Firebase.auth.currentUser!!.delete()
        userVM.deleteUser(uid)
      }
    }
  }

  @Test
  fun testEditProfile() {
    composeTestRule.setContent { EditProfile(NavigationActions(rememberNavController())) }

    // Wait for the page to load
    composeTestRule.waitUntil(timeoutMillis = 10000) {
      composeTestRule.onNodeWithTag("Profile Picture").isDisplayed()
    }

    // Test that the ui is correctly displayed. fill in the fields and click on Done
    composeTestRule.onNodeWithTag("Profile Picture").assertIsDisplayed()
    composeTestRule.onNodeWithText("Edit Tags").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("EditTagsButton").assertIsDisplayed().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("TagList").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText("Save").assertIsDisplayed().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("Country").performScrollTo().assertIsDisplayed().performClick()

    composeTestRule.onAllNodesWithTag("CountryItem")[1].assertIsDisplayed().performClick()

    composeTestRule
        .onNodeWithText("Phone Number")
        .performScrollTo()
        .assertIsDisplayed()
        .performTextInput("+1234567890")
    composeTestRule
        .onNodeWithText("Username")
        .performScrollTo()
        .assertIsDisplayed()
        .performTextInput("usesrname")
    composeTestRule
        .onNodeWithText("Last Name")
        .performScrollTo()
        .assertIsDisplayed()
        .performTextInput("lastname")
    composeTestRule
        .onNodeWithText("First Name")
        .performScrollTo()
        .assertIsDisplayed()
        .performTextInput("firstname")
    composeTestRule.onNodeWithText("Done").assertIsDisplayed().performClick()
  }
}
