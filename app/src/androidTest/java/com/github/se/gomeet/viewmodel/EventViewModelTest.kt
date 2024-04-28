package com.github.se.gomeet.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.location.Location
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.test.runTest
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

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
        uid)

    val events = eventViewModel.getAllEvents()!!.filter { it.title == title }

    TimeUnit.SECONDS.sleep(5)

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

  companion object {

    private lateinit var eventViewModel: EventViewModel
    private val title = "testevent"
    private val uid = "testuser"

    @BeforeClass
    @JvmStatic
    fun setup() {
      eventViewModel = EventViewModel(uid)
    }
  }
}
