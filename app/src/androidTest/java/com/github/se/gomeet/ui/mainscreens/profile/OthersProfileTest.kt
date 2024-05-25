package com.github.se.gomeet.ui.mainscreens.profile

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OthersProfileTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  companion object {
    private const val email1 = "user1@othersprofiletest.com"
    private const val pwd1 = "123456"
    private lateinit var uid1: String
    private const val username1 = "othersprofiletest1"

    private const val email2 = "user2@othersprofiletest.com"
    private const val pwd2 = "654321"
    private lateinit var uid2: String
    private const val username2 = "othersrofiletest2"

    private lateinit var userVM: UserViewModel
    private lateinit var eventVM: EventViewModel

    @BeforeClass
    @JvmStatic
    fun setUp() = runBlocking {

      // Create two new users
      Firebase.auth.createUserWithEmailAndPassword(email1, pwd1).await()
      uid1 = Firebase.auth.currentUser!!.uid
      userVM = UserViewModel(uid1)
      eventVM = EventViewModel(uid1)

      Firebase.auth.createUserWithEmailAndPassword(email2, pwd2).await()
      uid2 = Firebase.auth.currentUser!!.uid

      // Add the users to the view model
      userVM.createUserIfNew(
          uid1,
          username1,
          "testfirstname",
          "testlastname",
          email1,
          "testphonenumber",
          "testcountry")

      userVM.createUserIfNew(
          uid2,
          username2,
          "testfirstname2",
          "testlastname2",
          email2,
          "testphonenumber2",
          "testcountry2")

      // Sign in with user1,
      Firebase.auth.signInWithEmailAndPassword(email1, pwd1).await()
      TimeUnit.SECONDS.sleep(1)
    }

    @AfterClass
    @JvmStatic
    fun tearDown() = runBlocking {
      // clean up the users
      Firebase.auth.currentUser?.delete()?.await()
      userVM.deleteUser(uid1)
      userVM.deleteUser(uid2)
      Firebase.auth.signInWithEmailAndPassword(email2, pwd2).await()
      Firebase.auth.currentUser?.delete()?.await()
      return@runBlocking
    }
  }

  @Test
  fun testOthersProfile() {
    // Viewing the profile of user2
    composeTestRule.setContent {
      OthersProfile(NavigationActions(rememberNavController()), uid2, userVM, eventVM)
    }
    // Wait for the page to load
    composeTestRule.waitUntil { composeTestRule.onNodeWithTag("Profile Picture").isDisplayed() }

    // Test that the ui is correctly displayed
    composeTestRule.onNodeWithTag("Profile Picture").assertIsDisplayed()
    composeTestRule.onNodeWithTag("TopBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("UserInfo").assertIsDisplayed()
    composeTestRule.onNodeWithText("Follow").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onNodeWithText("Message").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onAllNodesWithTag("EventsListHeader").assertCountEquals(2)
  }
}
