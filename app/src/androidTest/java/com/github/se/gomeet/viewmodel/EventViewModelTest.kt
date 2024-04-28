package com.github.se.gomeet.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.location.Location
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class EventViewModelTest {

  @Test
  fun test() = runTest {

      // test getAllEvents and createEvent
      eventViewModel.createEvent(
          title,
          "description",
          Location(0.0, 0.0, "name"),
          LocalDate.of(2024, 4, 19),
          0.0,
          "url",
          emptyList(),
          emptyList(),
          0,
          false,
          emptyList(),
          emptyList(),
          null,
          "testuser")

    val events = eventViewModel.getAllEvents()!!.filter { it.title == title }

    assert(events.isNotEmpty())

    // test getEvent
    val uid = events[0].uid
    var event = eventViewModel.getEvent(uid)

    assert(event != null)
    assert(event!!.uid == uid)
    assert(event.title == title)

    assert(eventViewModel.getEvent("this_event_does_not_exist") == null)

    // test editEvent
    val newTitle = "newtestevent"
    val newEvent =
        Event(
            event.uid,
            event.creator,
            newTitle,
            event.description,
            event.location,
            event.date,
            event.price,
            event.url,
            event.participants,
            event.visibleToIfPrivate,
            event.maxParticipants,
            event.public,
            event.tags,
            event.images)

    eventViewModel.editEvent(newEvent)
    event = eventViewModel.getEvent(uid)

    assert(event != null)
    assert(event!!.uid == uid)
    assert(event.title == newTitle)

    // test removeEvent
    eventViewModel.removeEvent(uid)
    event = eventViewModel.getEvent(uid)

    assert(event == null)
  }


    companion object{

        private lateinit var eventViewModel: EventViewModel
        private val title = "testevent"

        @BeforeClass
        @JvmStatic
        fun setup() {
            Firebase.firestore.useEmulator("10.0.2.2", 8080)
            Firebase.storage.useEmulator("10.0.2.2", 9199)
            Firebase.auth.useEmulator("10.0.2.2", 9099)
            eventViewModel = EventViewModel("testuser")

        }
    }


}
