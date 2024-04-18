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

class UserViewModel(private val uid: String? = null) : ViewModel() {
  private val db = UserFirebaseConnection(Firebase.firestore)

  suspend fun getUser(uidQuery: String): GoMeetUser? {
    return try {
      val event = CompletableDeferred<GoMeetUser?>()
      db.getUser(uidQuery) { t -> event.complete(t) }
      event.await()
    } catch (e: Exception) {
      null
    }
  }

  fun createUser(username: String) {
    CoroutineScope(Dispatchers.IO).launch {
      try {
        val user =
            GoMeetUser(
                uid = uid!!,
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

  fun sendRequest(user: GoMeetUser) {}

  fun remove(user: GoMeetUser) {}

  fun accept(user: GoMeetUser) {}

  fun reject(user: GoMeetUser) {}
}
