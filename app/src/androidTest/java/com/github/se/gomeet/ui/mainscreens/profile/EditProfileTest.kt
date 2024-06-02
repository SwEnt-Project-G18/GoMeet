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
import com.github.se.gomeet.R
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.ui.navigation.NavigationActions
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
class EditProfileTest {
  @get:Rule val composeTestRule = createComposeRule()

  companion object {
    private lateinit var userVM: UserViewModel
    private lateinit var uid: String

    private val usr = "editprofile@test.com"
    private val pwd = "123456"

    @BeforeClass
    @JvmStatic
    fun setUp() = runBlocking {

      // Create a new user and sign in
      Firebase.auth.createUserWithEmailAndPassword(usr, pwd).await()
      Firebase.auth.signInWithEmailAndPassword(usr, pwd).await()

      // Add the user to the view model
      uid = Firebase.auth.currentUser!!.uid
      userVM = UserViewModel(uid)
      userVM.createUserIfNew(uid, "a", "b", "c", usr, "4567", "Angola")
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
  fun testEditProfile() {
    composeTestRule.setContent { EditProfile(NavigationActions(rememberNavController()), userVM) }

    // Wait for the page to load
    composeTestRule.waitUntil(timeoutMillis = 10000) {
      composeTestRule.onNodeWithTag("Profile Picture").isDisplayed()
    }

    // Test that the ui is correctly displayed. fill in the fields and click on Done
    composeTestRule.onNodeWithTag("Profile Picture").assertIsDisplayed()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.edit_tags))
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag("EditTagsButton").assertIsDisplayed().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("TagList").assertIsDisplayed().performClick()
    composeTestRule
        .onNodeWithText(getResourceString(R.string.save))
        .assertIsDisplayed()
        .performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("Country").performScrollTo().assertIsDisplayed().performClick()

    composeTestRule.onAllNodesWithTag("CountryItem")[1].assertIsDisplayed().performClick()

    composeTestRule
        .onNodeWithText(getResourceString(R.string.phone_number_text_field))
        .performScrollTo()
        .assertIsDisplayed()
        .performTextInput("+1234567890")
    composeTestRule
        .onNodeWithText(getResourceString(R.string.username_text_field))
        .performScrollTo()
        .assertIsDisplayed()
        .performTextInput("usesrname")
    composeTestRule
        .onNodeWithText(getResourceString(R.string.last_name_text_field))
        .performScrollTo()
        .assertIsDisplayed()
        .performTextInput("lastname")
    composeTestRule
        .onNodeWithText(getResourceString(R.string.first_name_text_field))
        .performScrollTo()
        .assertIsDisplayed()
        .performTextInput("firstname")
    composeTestRule
        .onNodeWithText(getResourceString(R.string.done))
        .assertIsDisplayed()
        .performClick()
  }
}
