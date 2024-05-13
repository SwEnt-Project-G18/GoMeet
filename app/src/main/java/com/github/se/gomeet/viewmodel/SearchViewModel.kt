package com.github.se.gomeet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.location.Location
import com.github.se.gomeet.model.repository.EventRepository
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.model.user.GoMeetUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SearchViewModel() : ViewModel() {
  private val _searchText = MutableStateFlow("")
  val searchText = _searchText.asStateFlow()
  val userRepository = UserRepository(Firebase.firestore)
  val eventsRepository = EventRepository(Firebase.firestore)
  var allUsersList: List<GoMeetUser>? =
      listOf(
          GoMeetUser(
              "1",
              "username",
              "Buzz",
              "Aldrin",
              "buzz.aldrin@nasa.gov",
              "202 062 0690",
              "United States",
              emptyList(),
              emptyList(),
              emptyList(),
              emptyList(),
              emptyList(),
              emptyList()),
          GoMeetUser(
              "2",
              "username",
              "Michael",
              "Collins",
              "email",
              "202 062 0690",
              "United States",
              emptyList(),
              emptyList(),
              emptyList(),
              emptyList(),
              emptyList(),
              emptyList()),
          GoMeetUser(
              "3",
              "username",
              "Jim",
              "Lovell",
              "email",
              "phoneNumber",
              "country",
              emptyList(),
              emptyList(),
              emptyList(),
              emptyList(),
              emptyList(),
              emptyList()),
          GoMeetUser(
              "4",
              "username",
              "Ed",
              "White",
              "email",
              "phoneNumber",
              "country",
              emptyList(),
              emptyList(),
              emptyList(),
              emptyList(),
              emptyList(),
              emptyList()))
  var allPublicEventsList: List<Event>? =
      listOf(
          Event(
              "1",
              "creator",
              "Saturn V Launch",
              "description",
              Location(.0, .0, "CCAFS"),
              LocalDate.now(),
              0.0,
              "url",
              emptyList(),
              emptyList(),
              0,
              true,
              emptyList(),
              emptyList()),
          Event(
              "2",
              "creator",
              "Saturn 1B Launch",
              "description",
              Location(.0, .0, "CCAFS"),
              LocalDate.now(),
              0.0,
              "url",
              emptyList(),
              emptyList(),
              0,
              true,
              emptyList(),
              emptyList()),
          Event(
              "3",
              "creator",
              "Delta II Launch",
              "description",
              Location(.0, .0, "CCAFS"),
              LocalDate.now(),
              0.0,
              "url",
              emptyList(),
              emptyList(),
              0,
              true,
              emptyList(),
              emptyList()))

  private val _searchQuery = MutableStateFlow<List<SearchableItem>>(emptyList())

  init {
    _searchQuery.value =
        (allUsersList?.map { SearchableItem.User(it) } ?: emptyList()) +
            (allPublicEventsList?.map { SearchableItem.Event(it) } ?: emptyList())

    // getAllUsers()
    // getAllEvents()
    // println("### Users: ${allUsersList.toString()}")
  }

  private fun getAllUsers() {
    viewModelScope.launch { userRepository.getAllUsers { users -> allUsersList = users } }
  }

  private fun getAllEvents() {
    viewModelScope.launch {
      eventsRepository.getAllEvents { events -> allPublicEventsList = events }
    }
  }

  private val _isSearching = MutableStateFlow(false)
  val isSearching = _isSearching.asStateFlow()

  val searchQuery =
      searchText
          .debounce(1000L)
          .onEach { _isSearching.update { true } }
          .combine(_searchQuery) { text, query ->
            if (text.isBlank()) {
              query
            } else {
              delay(1000L)
              query.filter { it.doesMatchSearchQuery(text) }
            }
          }
          .onEach { _isSearching.update { false } }
          .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _searchQuery.value)

  fun onSearchTextChange(text: String) {
    _searchText.value = text
  }

  sealed class SearchableItem {
    data class User(val user: GoMeetUser) : SearchableItem()

    data class Event(val event: com.github.se.gomeet.model.event.Event) : SearchableItem()
  }

  private fun SearchableItem.doesMatchSearchQuery(query: String): Boolean {
    return when (this) {
      is SearchableItem.User -> user.doesMatchSearchQuery(query)
      is SearchableItem.Event -> event.doesMatchSearchQuery(query)
    }
  }
}
