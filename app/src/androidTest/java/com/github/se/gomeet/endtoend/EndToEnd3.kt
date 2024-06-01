package com.github.se.gomeet.endtoend

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.se.gomeet.MainActivity
import com.github.se.gomeet.model.event.location.Location
import com.github.se.gomeet.screens.AddParticipantsScreen
import com.github.se.gomeet.screens.CreateEventScreen
import com.github.se.gomeet.screens.CreateScreen
import com.github.se.gomeet.screens.EventInfoScreen
import com.github.se.gomeet.screens.EventsScreen
import com.github.se.gomeet.screens.ExploreScreen
import com.github.se.gomeet.screens.LoginScreenScreen
import com.github.se.gomeet.screens.ManageInvitesScreen
import com.github.se.gomeet.screens.WelcomeScreenScreen
import com.github.se.gomeet.viewmodel.AuthViewModel
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This end to end test tests that a user can create/delete a post and add participants to an event
 * they created as well as adding participants when they create a new private event.
 */
@RunWith(AndroidJUnit4::class)
class EndToEndTest3 : TestCase() {
  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()
  @get:Rule
  var permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

  companion object {
    private const val email1 = "user1@test3.com"
    private const val pwd1 = "123456"
    private var uid1 = ""
    private const val username1 = "test3_user1"

    private const val email2 = "user2@test3.com"
    private const val pwd2 = "654321"
    private var uid2 = ""
    private const val username2 = "test3_user2"

    private lateinit var userVM: UserViewModel
    private lateinit var eventVM: EventViewModel
    private val authViewModel = AuthViewModel()

    @JvmStatic
    @BeforeClass
    fun setup() = runBlocking {
      // create two new users
      Firebase.auth.createUserWithEmailAndPassword(email2, pwd2).await()
      uid2 = Firebase.auth.currentUser!!.uid

      Firebase.auth.createUserWithEmailAndPassword(email1, pwd1).await()
      uid1 = Firebase.auth.currentUser!!.uid

      userVM = UserViewModel(uid2)

      // Create the users
      userVM.createUserIfNew(
          uid1,
          username1,
          "testfirstname",
          "testlastname",
          email1,
          "testphonenumber",
          "testcountry")
      while (userVM.getUser(uid1) == null) {
        TimeUnit.SECONDS.sleep(1)
      }

      userVM.createUserIfNew(
          uid2,
          username2,
          "testfirstname2",
          "testlastname2",
          email2,
          "testphonenumber2",
          "testcountry2")

      while (userVM.getUser(uid2) == null) {
        TimeUnit.SECONDS.sleep(1)
      }
      // user2 follows user1
      userVM.follow(uid1)
      while (userVM.getUser(uid1)!!.followers.isEmpty()) {
        TimeUnit.SECONDS.sleep(1)
      }

      // user1 creates an event and follows user2
      Firebase.auth.signInWithEmailAndPassword(email1, pwd1).await()
      userVM = UserViewModel(uid1)
      userVM.follow(uid2)
      while (userVM.getUser(uid1)!!.following.isEmpty()) {
        TimeUnit.SECONDS.sleep(1)
      }

      eventVM = EventViewModel(uid1)
      eventVM.createEvent(
          "title",
          "description",
          Location(0.0, 0.0, "location"),
          LocalDate.of(2025, 3, 30),
          LocalTime.now(),
          0.0,
          "url",
          emptyList(),
          emptyList(),
          emptyList(),
          0,
          true,
          emptyList(),
          emptyList(),
          null,
          userVM,
          "eventuid1")
      while (userVM.getUser(uid1)!!.myEvents.isEmpty()) {
        TimeUnit.SECONDS.sleep(1)
      }

      authViewModel.signOut()

      TimeUnit.SECONDS.sleep(1)
    }

    @AfterClass
    @JvmStatic
    fun tearDown() = runBlocking {

      // clean up the event
      eventVM.getAllEvents()?.forEach { eventVM.removeEvent(it.eventID) }

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
  fun test() = run {
    ComposeScreen.onComposeScreen<WelcomeScreenScreen>(composeTestRule) {
      step("Click on the log in button") {
        composeTestRule.onNodeWithText("Log In").assertIsDisplayed().performClick()
      }
    }

    ComposeScreen.onComposeScreen<LoginScreenScreen>(composeTestRule) {
      step("Log in with email and password") {
        composeTestRule.onNodeWithText("Log In").assertIsDisplayed().assertIsNotEnabled()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed().performTextInput(email1)
        composeTestRule.onNodeWithText("Password").assertIsDisplayed().performTextInput(pwd1)
        composeTestRule.onNodeWithText("Log In").assertIsEnabled().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil(timeoutMillis = 10000) {
          composeTestRule.onNodeWithTag("ExploreUI").isDisplayed()
        }
      }
    }

    ComposeScreen.onComposeScreen<ExploreScreen>(composeTestRule) {
      step("Go to Events") {
        composeTestRule.onNodeWithTag("Events").assertIsDisplayed().performClick()
      }
    }
    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      step("View the info page of the event") {
        composeTestRule.waitForIdle()
        composeTestRule.onAllNodesWithText("Events")[0].performClick()
        composeTestRule.waitUntil(timeoutMillis = 10000) {
          composeTestRule.onAllNodesWithTag("Card")[0].isDisplayed()
        }
        composeTestRule.onAllNodesWithTag("Card")[0].performClick()
      }
    }

    ComposeScreen.onComposeScreen<EventInfoScreen>(composeTestRule) {
      composeTestRule.waitUntil(timeoutMillis = 10000) {
        composeTestRule.onNodeWithTag("EventHeader").isDisplayed()
      }

      step("Test that the dialog that appears when deleting an event is displayed properly") {
        composeTestRule.onNodeWithContentDescription("Delete").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Delete Event").assertIsDisplayed()
        composeTestRule.onNodeWithTag("DeleteEventConfirmationText").assertIsDisplayed()
        composeTestRule.onNodeWithText("Confirm").assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()
      }
      step("Create a post and then delete it") {
        composeTestRule.onNodeWithText("Posts").assertIsDisplayed()
        composeTestRule.onNodeWithTag("NoPostsText").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Add Post").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()
        composeTestRule
            .onNodeWithContentDescription("Cancel")
            .assertIsDisplayed()
            .assertHasClickAction()
        composeTestRule.onNodeWithText("Add a Post").assertIsDisplayed()
        composeTestRule.onNodeWithTag("AddPostUserInfo").assertIsDisplayed()
        composeTestRule
            .onNodeWithContentDescription("Add Image")
            .assertIsDisplayed()
            .assertHasClickAction()
        composeTestRule.onNodeWithText("What's new ?").assertIsDisplayed().performTextInput("test")
        composeTestRule.onNodeWithText("Post").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("EventPostUserInfo").assertIsDisplayed()
        composeTestRule.onNodeWithText("test").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Like").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithTag("PostDate").assertIsDisplayed()
        composeTestRule
            .onNodeWithContentDescription("Delete Post")
            .assertIsDisplayed()
            .performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Delete Post").assertIsDisplayed()
        composeTestRule.onNodeWithTag("DeletePostConfirmationText").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText("Confirm").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("NoPostsText").assertIsDisplayed()
      }

      step("Go to ManageInvites by clicking on Handle Participants") {
        composeTestRule.onNodeWithText("Edit My Event").assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText("Handle Participants").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()
      }
    }

    ComposeScreen.onComposeScreen<ManageInvitesScreen>(composeTestRule) {
      composeTestRule.waitUntil(timeoutMillis = 10000) {
        composeTestRule.onNodeWithTag("UserInviteWidget").isDisplayed()
      }

      step("Test the ui of ManageInvites and invite a user to the event") {
        composeTestRule.onNodeWithText("Manage Invites").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pending").assertIsDisplayed()
        composeTestRule.onNodeWithText("Accepted").assertIsDisplayed()
        composeTestRule.onNodeWithText("Refused").assertIsDisplayed()
        composeTestRule.onNodeWithText("To Invite").assertIsDisplayed()
        composeTestRule.onNodeWithText("@$username2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Invite").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithContentDescription("Go back").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()
      }

      ComposeScreen.onComposeScreen<EventInfoScreen>(composeTestRule) {
        step("Refresh ManageInvites") {
          composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onNodeWithTag("EventHeader").isDisplayed()
          }
          composeTestRule.onNodeWithText("Edit My Event").assertIsDisplayed().assertHasClickAction()
          composeTestRule.onNodeWithText("Handle Participants").assertIsDisplayed().performClick()
          composeTestRule.waitForIdle()
        }
      }

      step("Verify that the invitation appears in Pending") {
        composeTestRule.onNodeWithText("Pending").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil {
          composeTestRule.onNodeWithTag("UserInviteWidget").isDisplayed()
        }
        composeTestRule.onNodeWithTag("UserInviteWidget").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Go back").performClick()
        composeTestRule.waitForIdle()
      }
    }

    ComposeScreen.onComposeScreen<EventInfoScreen>(composeTestRule) {
      step("Go back to Events") {
        composeTestRule.waitUntil(timeoutMillis = 10000) {
          composeTestRule.onNodeWithTag("EventHeader").isDisplayed()
        }
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.waitForIdle()
      }
    }

    ComposeScreen.onComposeScreen<EventsScreen>(composeTestRule) {
      step("Create a new Event") {
        composeTestRule.onNodeWithTag("CreateEventButton").performClick()
        composeTestRule.waitForIdle()
      }
    }

    ComposeScreen.onComposeScreen<CreateScreen>(composeTestRule) {
      step("Create a private event") {
        composeTestRule.onNodeWithText("Private").performClick()
        composeTestRule.waitForIdle()
      }
    }

    ComposeScreen.onComposeScreen<CreateEventScreen>(composeTestRule) {
      step("Add Participants") {
        composeTestRule.onNodeWithContentDescription("Add Participants").performClick()
        composeTestRule.waitForIdle()
      }
    }

    ComposeScreen.onComposeScreen<AddParticipantsScreen>(composeTestRule) {
      step("Check that the ui of AddParticipants is correctly displayed and invite a user") {
        composeTestRule.waitUntil(timeoutMillis = 10000) {
          composeTestRule.onNodeWithText("Search").isDisplayed()
        }

        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add Participants").assertExists()
        composeTestRule.onNodeWithText("Search").assertIsDisplayed()
        composeTestRule.onNodeWithTag("InviteUserWidget").assertIsDisplayed()
        composeTestRule.onNodeWithText("testfirstname2", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText(username2, substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Invite").assertExists().performClick()
      }
    }
  }
}
