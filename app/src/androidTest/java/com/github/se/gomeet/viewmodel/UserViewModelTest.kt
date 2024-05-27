package com.github.se.gomeet.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.location.Location
import com.github.se.gomeet.model.user.GoMeetUser
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserViewModelTest {
  companion object {
    private const val uid = "UserViewModelTestUser"
    private const val username = "userviewmodel"
    private const val firstname = "testfirstname"
    private const val lastname = "testlastname"
    private const val email = "testemail"
    private const val phonenumber = "testphonenumber"
    private const val country = "testcountry"
    private val user =
        GoMeetUser(
            uid,
            username,
            firstname,
            lastname,
            email,
            phonenumber,
            country,
            emptyList(),
            emptyList(),
            emptySet(),
            emptyList(),
            emptyList(),
            emptyList(),
            tags = emptyList())
    private val event1 =
        Event(
            "event6",
            uid,
            "",
            "",
            Location(0.0, 0.0, ""),
            LocalDate.now(),
            LocalTime.now(),
            0.0,
            "",
            emptyList(),
            emptyList(),
            emptyList(),
            0,
            true)
    private val event2 =
        Event(
            "event7",
            uid,
            "",
            "",
            Location(0.0, 0.0, ""),
            LocalDate.now(),
            LocalTime.now(),
            0.0,
            "",
            emptyList(),
            emptyList(),
            emptyList(),
            0,
            true)

    private val userVM = UserViewModel(uid)
    private val eventVM = EventViewModel(uid)

    @BeforeClass
    @JvmStatic
    fun setup() = runBlocking {
      userVM.createUserIfNew(uid, username, firstname, lastname, email, phonenumber, country, "")
      eventVM.editEvent(event1)
      eventVM.editEvent(event2)
      TimeUnit.SECONDS.sleep(1)
    }

    @AfterClass
    @JvmStatic
    fun tearDown() = runBlocking {
      // Clean up the user
      userVM.deleteUser(uid)
    }
  }

  @Test
  fun getFollowersTest() {
    val otherUid = "AnotherUser1"

    // Add another user to the following list
    runBlocking { userVM.editUser(userVM.getUser(uid)!!.copy(following = listOf(otherUid))) }

    // Get the other user's followers from the view model
    var followers: List<GoMeetUser>
    runBlocking { followers = userVM.getFollowers(otherUid) }

    // Make sure that it contains the main user
    while (!followers.any { it.uid == uid }) {
      TimeUnit.SECONDS.sleep(1)
    }
    assert(followers.any { it.uid == uid })
  }

  @Test
  fun getAllUsersTest() {
    // Get all the users from the view model
    var allUsers: List<GoMeetUser>?
    runBlocking { allUsers = userVM.getAllUsers() }

    // Make sure that the created user is present
    runBlocking {
      assert(allUsers != null)
      assert(allUsers!!.any { it.uid == userVM.getUser(uid)!!.uid })
    }
  }

  @Test
  fun joinEventTest() {
    val eventId = "event1"

    // Join an event
    runBlocking { userVM.joinEvent(eventId, uid) }

    // Verify that the user's joinedEvents list was correctly updated
    runBlocking { assert(userVM.getUser(uid)!!.joinedEvents.contains(eventId)) }
  }

  @Test
  fun userCreatesEventTest() {
    val eventId = "event2"

    // Create an event
    runBlocking { userVM.userCreatesEvent(eventId, uid) }

    // Verify that the user's myEvents list was correctly updated
    runBlocking { assert(userVM.getUser(uid)!!.myEvents.contains(eventId)) }
  }

  @Test
  fun gotInvitationTest() {
    val eventId = "event3"

    // Invite the user to the event
    runBlocking { userVM.gotInvitation(eventId, uid) }

    // Verify that the user's pendingRequests was correctly updated
    runBlocking { assert(userVM.getUser(uid)!!.pendingRequests.any { it.eventId == eventId }) }

    // Make sure that the user can't be invited twice to the same event
    runBlocking { userVM.gotInvitation(eventId, uid) }
    runBlocking {
      assert(userVM.getUser(uid)!!.pendingRequests.count { it.eventId == eventId } == 1)
    }
  }

  @Test
  fun gotKickedFromEventTest() {
    val eventId = "event4"

    // Join the event
    runBlocking { userVM.joinEvent(eventId, uid) }

    // Kick the user from the event
    runBlocking { userVM.gotKickedFromEvent(eventId, uid) }

    // Make sure that the event is no longer in the user's joinedEvents list
    runBlocking { assert(!userVM.getUser(uid)!!.joinedEvents.any { it == eventId }) }
  }

  @Test
  fun invitationCanceledTest() {
    val eventId = "event5"

    // Invite the user to the event
    runBlocking { userVM.gotInvitation(eventId, uid) }

    // Cancel the invite
    runBlocking { userVM.invitationCanceled(eventId, uid) }

    // Make sure that the user is no longer invited to the event
    runBlocking { assert(!userVM.getUser(uid)!!.pendingRequests.any { it.eventId == eventId }) }
  }

  /**
   * @Test fun userAcceptsInvitationTest() { val eventId = "event6"
   *
   * // Invite the user to the event runBlocking { userVM.gotInvitation(eventId, uid) }
   *
   * // Make the user accept the invitation runBlocking { userVM.userAcceptsInvitation(event1, user,
   * eventVM) }
   *
   * // Verify that the event appears in the user's joinedEvents list runBlocking { while
   * (!userVM.getUser(uid)!!.joinedEvents.contains(eventId)) { TimeUnit.SECONDS.sleep(1) }
   * assert(userVM.getUser(uid)!!.joinedEvents.contains(eventId)) } }
   *
   * @Test fun userRefusesInvitationTest() { val eventId = "event7"
   *
   * // Invite the user to the event runBlocking { userVM.gotInvitation(eventId, uid) }
   *
   * // Make the user refuse the invitation runBlocking { userVM.userRefusesInvitation(event2, user,
   * eventVM) }
   *
   * // Verify that the invitation is no longer in pendingRequests runBlocking { assert(
   * userVM.getUser(uid)!!.pendingRequests.any { it.eventId == eventId && it.status ==
   * InviteStatus.REFUSED }) }
   *
   * // Verify that the event doesn't appear in the user's joinedEvents list runBlocking {
   * assert(!userVM.getUser(uid)!!.joinedEvents.contains(eventId)) assert(
   * userVM.getUser(uid)!!.pendingRequests.any { it.eventId == eventId && it.status ==
   * InviteStatus.REFUSED }) } }*
   */
  @Test
  fun getUsernameTest() {
    var usrname: String?

    runBlocking { usrname = userVM.getUsername(uid) }

    assert(usrname != null)
    assert(usrname == username)
  }
}
