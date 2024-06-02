package com.github.se.gomeet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.repository.EventRepository
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.model.user.GoMeetUser
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

/** View model for advanced searching */
@OptIn(FlowPreview::class)
class SearchViewModel : ViewModel() {
  private val _searchText = MutableStateFlow("")
  val searchText = _searchText.asStateFlow()
  private var allUsersList: List<GoMeetUser>? = emptyList()
  private var allPublicEventsList: List<Event>? = emptyList()
  private val _searchQuery = MutableStateFlow<List<SearchableItem>>(emptyList())

  init {
    viewModelScope.launch {
      getAllUsers()
      getAllEvents()
    }
  }

  /**
   * Perform a search for the given query
   *
   * @param query the search query
   */
  fun performSearch(query: String) {
    _searchText.value = query
    val filteredUsers = allUsersList?.filter { it.doesMatchSearchQuery(query) } ?: emptyList()
    val filteredEvents =
        allPublicEventsList?.filter { it.doesMatchSearchQuery(query) } ?: emptyList()
    _searchQuery.value =
        filteredUsers.map { SearchableItem.User(it) } +
            filteredEvents.map { SearchableItem.Event(it) }
  }

  /** Get all users from the database */
  fun getAllUsers() {
    UserRepository.getAllUsers { users: List<GoMeetUser> -> allUsersList = users }
  }

  /** Get all events from the database */
  fun getAllEvents() {
    EventRepository.getAllEvents { events -> allPublicEventsList = events }
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

  /** Data class that represents a searchable item */
  sealed class SearchableItem {
    data class User(val user: GoMeetUser) : SearchableItem()

    data class Event(val event: com.github.se.gomeet.model.event.Event) : SearchableItem()
  }

  /**
   * Returns whether the item matched the search query
   *
   * @param query search query
   * @return true if the item matched the search query, false otherwise
   */
  private fun SearchableItem.doesMatchSearchQuery(query: String): Boolean {
    return when (this) {
      is SearchableItem.User -> user.doesMatchSearchQuery(query)
      is SearchableItem.Event -> event.doesMatchSearchQuery(query)
    }
  }
}
