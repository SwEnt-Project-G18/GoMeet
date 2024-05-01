package com.github.se.gomeet.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.model.user.GoMeetUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel for the user. The viewModel is responsible for handling the logic that comes from the
 * UI and the repository.
 */
class UserViewModel : ViewModel() {
  private val db = UserRepository(Firebase.firestore)

  /**
   * Create a new user if the user is new.
   *
   * @param uid the user id
   * @param username the username
   */
  fun createUserIfNew(uid: String, username: String) {
    CoroutineScope(Dispatchers.IO).launch {
      if (getUser(uid) == null) {
        try {
          val user =
              GoMeetUser(
                  uid = uid,
                  username = username,
                  following = emptyList(),
                  followers = emptyList(),
                  pendingRequests = emptyList(),
                  joinedEvents = emptyList(),
                  myEvents = emptyList(),
                  myFavorites = emptyList())
          db.addUser(user)
        } catch (e: Exception) {
          Log.w(ContentValues.TAG, "Error adding user", e)
        }
      }
    }
  }

  suspend fun getUserFriends(uid: String): List<GoMeetUser> {
    val friends = mutableListOf<GoMeetUser>()
    db.getAllUsers { users ->
        for (user in users) {
          if (user.followers.contains(uid) || user.following.contains(uid)) {
            friends.add(user)
          }
        }
      }
    return friends
  }



  /**
   * Get the user with its id.
   *
   * @param uid the user id
   * @return the user
   */
  suspend fun getUser(uid: String): GoMeetUser? {
    return try {
      Log.d("UID IS", "User id is $uid")
      val event = CompletableDeferred<GoMeetUser?>()
      db.getUser(uid) { t -> event.complete(t) }
      event.await()
    } catch (e: Exception) {
      null
    }
  }

  /**
   * Edit the user.
   *
   * @param user the user to edit
   */
  fun editUser(user: GoMeetUser) {
    db.updateUser(user)
  }

  /**
   * Delete the user.
   *
   * @param uid the user id
   */
  fun deleteUser(uid: String) {
    db.removeUser(uid)
  }

  suspend fun joinEvent(eventId: String, userId: String) {
    try {
      val goMeetUser = getUser(userId)!!
      editUser(goMeetUser.copy(myEvents = goMeetUser.myEvents.plus(eventId)))
    } catch (e: Exception) {
      Log.w(ContentValues.TAG, "Couldn't join the event", e)
    }
  }

  suspend fun gotTicket(eventId: String, userId: String) {
    val goMeetUser = getUser(userId)!!
    editUser(goMeetUser.copy(joinedEvents = goMeetUser.joinedEvents.plus(eventId)))
  }

  // fun sendRequest() {
  // TODO
  // }

  // fun remove() {
  // TODO
  // }

  // fun accept() {
  // TODO
  // }

  // fun reject() {
  // TODO
  // }
}
