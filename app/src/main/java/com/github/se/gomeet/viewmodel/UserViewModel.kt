package com.github.se.gomeet.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.gomeet.UserFirebaseConnection
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.model.user.Request
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
  private val db = UserFirebaseConnection(Firebase.firestore)
  val currentUid by lazy { Firebase.auth.currentUser!!.uid }

  fun createUserIfNew(username: String) {
    CoroutineScope(Dispatchers.IO).launch {
      if (getUser(currentUid) == null) {
        try {
          val user =
              GoMeetUser(
                  uid = currentUid,
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

  suspend fun getUser(uid: String): GoMeetUser? {
    return try {
      val event = CompletableDeferred<GoMeetUser?>()
      db.getUser(uid) { t -> event.complete(t) }
      event.await()
    } catch (e: Exception) {
      null
    }
  }

  fun sendRequest(uid: String) {
    // TODO
  }

  fun remove(uid: String) {
    // TODO
  }

  fun accept(request: Request) {
    // TODO
  }

  fun reject(request: Request) {
    // TODO
  }
}
