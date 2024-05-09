package com.github.se.gomeet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.model.user.GoMeetUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
class SearchUserViewModel() : ViewModel() {
  private val _searchText = MutableStateFlow("")
  val searchText = _searchText.asStateFlow()
  val userRepository = UserRepository(Firebase.firestore)
  var allUsersList: List<GoMeetUser>? = null

    init{
        getAllUsers()
    }
  private fun getAllUsers() {
      viewModelScope.launch {
          userRepository.getAllUsers { users -> allUsersList = users }
      }
  }

  private val _isSearching = MutableStateFlow(false)
  val isSearching = _isSearching.asStateFlow()

  private val _persons = MutableStateFlow(allUsersList)
  val persons =
      searchText
          .debounce(1000L)
          .onEach { _isSearching.update { true } }
          .combine(_persons) { text, persons ->
            if (text.isBlank()) {
              persons
            } else {
              delay(2000L)
                persons?.filter { it.doesMatchSearchQuery(text) }
            }
          }
          .onEach { _isSearching.update { false } }
          .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _persons.value)

  fun onSearchTextChange(text: String) {
    _searchText.value = text
  }
}
