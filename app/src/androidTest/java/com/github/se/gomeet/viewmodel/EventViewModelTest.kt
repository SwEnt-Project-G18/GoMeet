package com.github.se.gomeet.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.location.Location
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventViewModelTest {

  private val title = "testevent2"
  private val uid = "testuid"

  @Test
  fun test() = runTest {
    val eventViewModel = EventViewModel(uid)

    // test getAllEvents and createEvent
    runBlocking {
      eventViewModel.createEvent(
          title,
          "description",
          Location(0.0, 0.0, "name"),
          LocalDate.of(2024, 4, 29),
          0.0,
          "url",
          emptyList(),
          emptyList(),
          0,
          false,
          emptyList(),
          emptyList(),
          null,
          UserViewModel(),
          uid)
    }

    var events: List<Event> = eventViewModel.getAllEvents()!!.filter { it.title == title }
    while (events.isEmpty()) {
      events = eventViewModel.getAllEvents()!!.filter { it.title == title }
    }

    assert(events.isNotEmpty())

    // test getEvent
    val uid = events[0].uid
    lateinit var event: Event
    event = eventViewModel.getEvent(uid)!!

    assert(event != null)
    assert(event.uid == uid)
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
    event = eventViewModel.getEvent(uid)!!

    assert(event != null)
    assert(event.uid == uid)
    assert(event.title == newTitle)

    // test removeEvent
    eventViewModel.removeEvent(uid)
    assert(eventViewModel.getEvent(uid) == null)
  }
}
