package com.github.se.gomeet.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.model.event.Event
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventViewModelTest {
  private val eventViewModel = EventViewModel("testuser")

  @Before
  fun addEvent() {
    /**
     * eventViewModel.createEvent( "title", "description", Location(0.0, 0.0, "name"),
     * LocalDate.of(2024, 4, 19), 0.0, "url", emptyList(), emptyList(), 0, false, emptyList(),
     * emptyList(), null)*
     */
  }

  @Test
  fun getEventTest() = runTest {
    var uid = "vXGZfEZ7g8lVhkIilZAT"
    var event = eventViewModel.getEvent(uid)

    assert(event != null)
    assert(event!!.uid == uid)

    uid = "this_event_does_not_exist"
    event = eventViewModel.getEvent(uid)

    assert(event == null)
  }

  @Test
  fun editEventTest() = runTest {
    val uid = "vXGZfEZ7g8lVhkIilZAT"
    var event = eventViewModel.getEvent(uid)
    val randomNumber = (0..Int.MAX_VALUE).random().toString()
    val newTitle = "test_event_dont_delete_$randomNumber"
    val newEvent =
        Event(
            event!!.uid,
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
    assert(event!!.title == newTitle)
  }

  @Test
  fun removeEventTest() =
      runTest {
        // TODO
      }
}
