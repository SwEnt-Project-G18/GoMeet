package com.github.se.gomeet.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.model.event.location.Location
import com.github.se.gomeet.model.repository.EventRepository
import com.github.se.gomeet.model.repository.UserRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
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
    private const val price = 0.0
    private const val url = "url"
    private val pendingParticipants = emptyList<String>()
    private val participants = emptyList<String>()
    private val visibleToIfPrivate = emptyList<String>()
    private const val maxParticipants = 1
    private const val public = false
    private val tags = emptyList<String>()
    private val images = emptyList<String>()
    private val imageUrl = null
    private val userVM = UserViewModel(UserRepository(Firebase.firestore))
    private const val eventId = "EventViewModelTestEvent"

    private const val uid = "EventViewModelTestUser"
    private val eventVM = EventViewModel(uid, EventRepository(Firebase.firestore))

    @BeforeClass
    @JvmStatic
    fun setup() {
      // Create an event
      runBlocking {
        eventVM.createEvent(
            title,
            description,
            location,
            date,
            price,
            url,
            pendingParticipants,
            participants,
            visibleToIfPrivate,
            maxParticipants,
            public,
            tags,
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
    runBlocking { eventVM.acceptInvitation(eventVM.getEvent(eventId)!!, userId) }

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
