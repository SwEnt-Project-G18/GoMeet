package com.github.se.gomeet.ui.mainscreens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
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
class ExploreTest {
  @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()
  @get:Rule
  var permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

  @Test
  fun uiElementsDisplayed() {
    lateinit var navController: NavHostController

    rule.setContent {
      navController = rememberNavController()
      Explore(
          nav = NavigationActions(navController),
          eventViewModel = EventViewModel(null, EventRepository(Firebase.firestore)))
    }

    rule.waitUntil(timeoutMillis = 10000) { rule.onNodeWithTag("Map").isDisplayed() }

    rule.onNodeWithTag("Map").assertIsDisplayed()
    rule.onNodeWithText("Search").assertIsDisplayed()
    rule.onNodeWithTag("CurrentLocationButton").assertIsDisplayed().performClick()
  }

  companion object {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var userViewModel: UserViewModel

    private const val email = "user@exploretest.com"
    private const val pwd = "123456"
    private var uid = ""

    @JvmStatic
    @BeforeClass
    fun setup() {
      TimeUnit.SECONDS.sleep(3)
      // create a new user
      userViewModel = UserViewModel(UserRepository(Firebase.firestore))
      var result = Firebase.auth.createUserWithEmailAndPassword(email, pwd)
      while (!result.isComplete) {
        TimeUnit.SECONDS.sleep(1)
      }
      uid = result.result.user!!.uid

      runBlocking {
        userViewModel.createUserIfNew(
            uid,
            "explore_test_user",
            "testfirstname",
            "testlastname",
            email,
            "testphonenumber",
            "testcountry")
      }

      // sign in as the new user
      result = Firebase.auth.signInWithEmailAndPassword(email, pwd)
      while (!result.isComplete) {
        TimeUnit.SECONDS.sleep(1)
      }

      eventViewModel = EventViewModel(uid, EventRepository(Firebase.firestore))
    }

    @AfterClass
    @JvmStatic
    fun tearDown() {
      // clean up the user
      userViewModel.deleteUser(uid)
      Firebase.auth.currentUser?.delete()
    }
  }
}
