package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.model.Tag
import com.github.se.gomeet.model.event.location.Location
import com.github.se.gomeet.model.repository.EventRepository
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

class ProfileTest {

  @After
  fun tearDown() {
    // clean up the event
    runBlocking { eventVM.getAllEvents()?.forEach { eventVM.removeEvent(it.eventID) } }

    // clean up the users
    runBlocking {
      Firebase.auth.currentUser?.delete()
      userVM.deleteUser(uid)
    }
  }

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testProfile() {
    composeTestRule.setContent {
      Profile(
          NavigationActions(rememberNavController()),
          userId = "null",
          UserViewModel(UserRepository(Firebase.firestore)),
          EventViewModel("null", EventRepository(Firebase.firestore)))
    }

    composeTestRule.waitUntil(timeoutMillis = 10000) {
      composeTestRule.onNodeWithTag("Profile Picture").isDisplayed()
    }
    composeTestRule.onNodeWithText("My Profile").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Profile Picture").assertIsDisplayed()
    composeTestRule.onNodeWithText("Edit Profile").assertIsDisplayed()
    composeTestRule.onNodeWithText("Share Profile").assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("EventsListHeader")[0].assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("EventsListHeader")[1].assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("EventListItem").assertCountEquals(2)
    for (i in 0..1) {
      composeTestRule.onAllNodesWithTag("EventListItem")[i].assertIsDisplayed()
    }
  }

  companion object {

    private const val email = "user@profiletest.com"
    private const val pwd = "123456"
    private var uid = ""
    private const val username = "username"

    private val userVM = UserViewModel(UserRepository(Firebase.firestore))
    private lateinit var eventVM: EventViewModel

    @JvmStatic
    @BeforeClass
    fun setup() {
      TimeUnit.SECONDS.sleep(3)
      runBlocking {
        // create two new users
        var result = Firebase.auth.createUserWithEmailAndPassword(email, pwd)
        while (!result.isComplete) {
          TimeUnit.SECONDS.sleep(1)
        }
        uid = result.result.user!!.uid

        userVM.createUserIfNew(
            uid, username, "testfirstname", "testlastname", email, "testphonenumber", "testcountry")
        TimeUnit.SECONDS.sleep(3)

        result = Firebase.auth.signInWithEmailAndPassword(email, pwd)
        while (!result.isComplete) {
          TimeUnit.SECONDS.sleep(1)
        }
        eventVM = EventViewModel(uid, EventRepository(Firebase.firestore))
        eventVM.createEvent(
            "title1",
            "description1",
            Location(0.0, 0.0, "location1"),
            LocalDate.of(2026, 1, 1),
            0.0,
            "url",
            emptyList(),
            emptyList(),
            emptyList(),
            1,
            true,
            emptyList(),
            emptyList(),
            null,
            userVM,
            "uid1")
        eventVM.createEvent(
            "title2",
            "description2",
            Location(0.0, 0.0, "location2"),
            LocalDate.of(2023, 1, 1),
            0.0,
            "uid2",
            emptyList(),
            emptyList(),
            emptyList(),
            1,
            true,
            emptyList(),
            emptyList(),
            null,
            userVM,
            uid)
        userVM.editUser(
            userVM
                .getUser(uid)!!
                .copy(myEvents = listOf("uid1", "uid2"), tags = listOf(Tag.entries[0].name)))
      }
      TimeUnit.SECONDS.sleep(3)
    }
  }
}
