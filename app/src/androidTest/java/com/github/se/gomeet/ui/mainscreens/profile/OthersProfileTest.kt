package com.github.se.gomeet.ui.mainscreens.profile

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventViewModel
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
class OthersProfileTest {
  @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun uiElementsDisplayed() {
    lateinit var navController: NavHostController

    rule.setContent {
      navController = rememberNavController()
      OthersProfile(NavigationActions(navController), uid2, UserViewModel(), EventViewModel())
    }

    rule.onNodeWithTag("TopBar").assertIsDisplayed()
    rule.onNodeWithTag("UserInfo").assertIsDisplayed()
    rule.onNodeWithText("Follow").assertIsDisplayed()
    rule.onNodeWithText("Message").assertIsDisplayed()
    rule.onNodeWithText("Tags").assertIsDisplayed()
    rule.onNodeWithTag("MoreUserInfo").assertIsDisplayed()
    rule.onNodeWithTag("TagList").assertIsDisplayed()
    rule.onAllNodesWithTag("EventsListHeader")[0].assertIsDisplayed()
    rule.onAllNodesWithTag("EventsListItems")[0].assertIsDisplayed()
  }

  companion object {
    private const val email1 = "user1@othersprofiletest.com"
    private const val pwd1 = "123456"
    private var uid1 = ""
    private const val username1 = "othersprofiletest_user1"

    private const val email2 = "user2@othersprofiletest.com"
    private const val pwd2 = "654321"
    private var uid2 = ""
    private const val username2 = "othersrofiletest_user2"

    private lateinit var userVM: UserViewModel

    @BeforeClass
    @JvmStatic
    fun setUp() {
      TimeUnit.SECONDS.sleep(3)
      userVM = UserViewModel()
      // create two new users
      var result = Firebase.auth.createUserWithEmailAndPassword(email1, pwd1)
      while (!result.isComplete) {}
      uid1 = result.result.user!!.uid
      result = Firebase.auth.createUserWithEmailAndPassword(email2, pwd2)
      while (!result.isComplete) {}
      uid2 = result.result.user!!.uid
      runBlocking {
        userVM.createUserIfNew(
            uid1,
            username1,
            "testfirstname",
            "testlastname",
            email1,
            "testphonenumber",
            "testcountry")
      }
      runBlocking {
        userVM.createUserIfNew(
            uid2,
            username2,
            "testfirstname2",
            "testlastname2",
            email2,
            "testphonenumber2",
            "testcountry2")
      }

      result = Firebase.auth.signInWithEmailAndPassword(email1, pwd1)
      while (!result.isComplete) {}

      // Ensure user is logged out before proceeding
      TimeUnit.SECONDS.sleep(2)
    }

    @AfterClass
    @JvmStatic
    fun tearDown() {
      // clean up the users
      Firebase.auth.currentUser?.delete()
      userVM.deleteUser(uid1)
      userVM.deleteUser(uid2)

      val result = Firebase.auth.signInWithEmailAndPassword(email2, pwd2)
      while (!result.isComplete) {}
      Firebase.auth.currentUser?.delete()
    }
  }
}
