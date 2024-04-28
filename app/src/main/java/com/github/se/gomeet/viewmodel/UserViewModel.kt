package com.github.se.gomeet.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.gomeet.UserFirebaseConnection
import com.github.se.gomeet.model.user.GoMeetUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel for the user. The viewModel is responsible for handling
 * the logic that comes from the UI and the repository.
 */
class UserViewModel : ViewModel() {
  private val db = UserFirebaseConnection(Firebase.firestore)

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
                  pendingRequests = emptyList())
          db.addUser(user)
        } catch (e: Exception) {
          Log.w(ContentValues.TAG, "Error adding user", e)
        }
      }
    }
  }

  /**
   * Get the user with its id.
   *
   * @param uid the user id
   *
   * @return the user
   */
  suspend fun getUser(uid: String): GoMeetUser? {
    return try {
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
