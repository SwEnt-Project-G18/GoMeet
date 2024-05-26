package com.github.se.gomeet.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.model.Tag
import com.github.se.gomeet.model.event.location.Location
import com.github.se.gomeet.model.user.GoMeetUser
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventViewModelTest {
  companion object {
    private const val title = "title"
    private const val description = "description"
    private val location = Location(0.0, 0.0, "location")
    private val date = LocalDate.of(9999, 3, 30)
    private val time = LocalTime.of(23, 40)
    private const val price = 0.0
    private const val url = "url"
    private val pendingParticipants = emptyList<String>()
    private val participants = emptyList<String>()
    private val visibleToIfPrivate = emptyList<String>()
    private const val maxParticipants = 1
    private const val public = false
    private val tags = MutableList<String>(0) { "" }
    private val images = emptyList<String>()
    private val imageUrl = null
    private const val eventId = "EventViewModelTestEvent"

    // 6 tags
    private val tagsList = listOf(Tag.Tag9, Tag.Tag0, Tag.Tag2, Tag.Tag5, Tag.Tag10, Tag.Tag12)

    private const val uid = "EventViewModelTestUser"
    private val eventVM = EventViewModel(uid)
    private val userVM = UserViewModel(uid)

    @BeforeClass
    @JvmStatic
    fun setup() {

      tagsList.forEach { tag -> tags.add(tag.tagName) }

      // Create an event
      runBlocking {
        eventVM.createEvent(
            title,
            description,
            location,
            date,
            time,
            price,
            url,
            pendingParticipants,
            participants,
            visibleToIfPrivate,
            maxParticipants,
            public,
            tags.subList(0, 5), // 5 tags
            images,
            imageUrl,
            userVM,
            eventId)
      }

      // Verify that the event was successfully created
      runBlocking { assert(eventVM.getEvent(eventId) != null) }
    }

    @AfterClass
    @JvmStatic
    fun tearDown() {
      // Clean up the events
      runBlocking { eventVM.getAllEvents()!!.forEach { eventVM.removeEvent(it.eventID) } }
    }
  }

  @Test
  fun testTrendsAlgo() = runTest {
    val userID = "user1"
    val username = "huge"
    val firstName = "Hugh"
    val lastName = "Jass"
    val email = "big@455.com"
    val phoneNumber = "12345"
    val country = "Switzerland"
    val currentUser =
        GoMeetUser(
            userID,
            username,
            firstName,
            lastName,
            email,
            phoneNumber,
            country,
            emptyList(),
            emptyList(),
            emptySet(),
            emptyList(),
            emptyList(),
            emptyList(),
            "",
            tags)
    userVM.createUserIfNew(userID, username, firstName, lastName, email, phoneNumber, country)

    val eventViewModel = EventViewModel(userID)
    val userViewModel = UserViewModel(userID)

    val eid1 = "01"
    val eid2 = "02"
    val eid3 = "03"

    val tags1 = tags.subList(0, 3) // 3 tags
    val tags2 = tags.subList(2, 4) // 2 tags
    val tags3 = tags // 6 tags
    // -> expected order: event 3, event 1, event 2

    eventViewModel.createEvent(
        "event1",
        "description",
        Location(0.0, 0.0, "name"),
        LocalDate.of(2024, 4, 29),
        LocalTime.now(),
        0.0,
        "url",
        emptyList(),
        emptyList(),
        emptyList(),
        0,
        false,
        tags1,
        emptyList(),
        null,
        userViewModel,
        eid1)

    eventViewModel.createEvent(
        "event2",
        "description",
        Location(0.0, 0.0, "name"),
        LocalDate.of(2024, 4, 29),
        LocalTime.now(),
        0.0,
        "url",
        emptyList(),
        emptyList(),
        emptyList(),
        0,
        true,
        tags2,
        emptyList(),
        null,
        userViewModel,
        eid2)

    eventViewModel.createEvent(
        "event3",
        "description",
        Location(0.0, 0.0, "name"),
        LocalDate.of(2024, 4, 29),
        LocalTime.now(),
        0.0,
        "url",
        emptyList(),
        emptyList(),
        emptyList(),
        0,
        true,
        tags3,
        emptyList(),
        null,
        userViewModel,
        eid3)

    TimeUnit.SECONDS.sleep(1)

    val events = eventViewModel.getAllEvents()!!.toMutableList()
    EventViewModel.sortEvents(currentUser.tags, events)
    assert(events[0].eventID == eid3)
    assert(events[1].eventID == eventId)
    assert(events[2].eventID == eid1)
    assert(events[3].eventID == eid2)

    userViewModel.deleteUser(userID)
  }

  @Test
  fun getEventImageUrlTest() {
    runBlocking { assert(eventVM.getEventImageUrl(eventId) == null) }
  }

  @Test
  fun getAllEventsTest() {
    runBlocking { assert(eventVM.getAllEvents()!!.any { it.eventID == eventId }) }
  }

  @Test
  fun editEventTest() {
    val newTitle = "newtitle"

    // Edit the event
    runBlocking { eventVM.editEvent(eventVM.getEvent(eventId)!!.copy(title = newTitle)) }

    // Verify that the event was changed accordingly
    runBlocking { assert(eventVM.getEvent(eventId)!!.title == newTitle) }
  }

  @Test
  fun joinEventTest() {
    // Join the event
    runBlocking { eventVM.joinEvent(eventVM.getEvent(eventId)!!, uid) }

    // Verify that the event's participants list was updated correctly
    runBlocking { assert(eventVM.getEvent(eventId)!!.participants.contains(uid)) }
  }

  @Test
  fun sendInvitationTest() {
    val userId = "uid1"

    // Send an invitation to the event
    runBlocking { eventVM.sendInvitation(eventVM.getEvent(eventId)!!, userId) }

    // Verify that the invited user was added to ehe event's pendingParticipants list
    runBlocking { assert(eventVM.getEvent(eventId)!!.pendingParticipants.contains(userId)) }
  }

  @Test
  fun acceptInvitationTest() {
    val userId = "uid2"

    // Invite a user to the event
    runBlocking { eventVM.sendInvitation(eventVM.getEvent(eventId)!!, userId) }

    // Make the user accept the invitation
    runBlocking { assert(eventVM.acceptInvitation(eventVM.getEvent(eventId)!!, userId)) }

    // Verify that the event's participants list was updated correctly
    runBlocking { assert(eventVM.getEvent(eventId)!!.participants.contains(userId)) }
  }

  @Test
  fun declineInvitationTest() {
    val userId = "uid3"

    // Invite a user to the event
    runBlocking { eventVM.sendInvitation(eventVM.getEvent(eventId)!!, userId) }

    // Make the user decline the invitation
    runBlocking { eventVM.declineInvitation(eventVM.getEvent(eventId)!!, userId) }

    // Verify that the event's pendingParticipants and participants lists were updated correctly
    runBlocking {
      assert(!eventVM.getEvent(eventId)!!.pendingParticipants.contains(userId))
      assert(!eventVM.getEvent(eventId)!!.participants.contains(userId))
    }
  }

  @Test
  fun kickParticipantTest() {
    val userId = "uid4"

    // Make the user join the event
    runBlocking { eventVM.joinEvent(eventVM.getEvent(eventId)!!, userId) }

    // Kick the user from the event
    runBlocking { eventVM.kickParticipant(eventVM.getEvent(eventId)!!, userId) }

    // Verify that the kicked user is no longer in the event's participants list
    runBlocking { assert(!eventVM.getEvent(eventId)!!.participants.contains(userId)) }
  }

  @Test
  fun cancelInvitationTest() {
    val userId = "uid5"

    // Invite a user to the event
    runBlocking { eventVM.sendInvitation(eventVM.getEvent(eventId)!!, userId) }

    // Cancel the invitation
    runBlocking { eventVM.cancelInvitation(eventVM.getEvent(eventId)!!, uid) }

    // Verify that the event's pendingParticipants list was correctly updated
    runBlocking { assert(!eventVM.getEvent(eventId)!!.pendingParticipants.contains(uid)) }
  }

  @Test
  fun locationTest() {
    val query = "q"
    var locationList: List<Location> = emptyList()
    val numberOfResults = 3

    // Get the location suggestions for the given query
    runBlocking { eventVM.location(query, numberOfResults) { result -> locationList = result } }

    // Make sure that the locations returned are correct
    while (locationList.isEmpty()) {
      TimeUnit.SECONDS.sleep(1)
    }
    assert(locationList.size == numberOfResults)
    locationList.forEach { location -> location.name.contains(query, ignoreCase = true) }
  }
}
