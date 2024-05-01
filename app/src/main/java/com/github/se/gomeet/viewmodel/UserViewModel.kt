package com.github.se.gomeet.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.model.user.GoMeetUser
import com.google.firebase.auth.ktx.auth
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
  fun createUserIfNew(
      uid: String,
      username: String,
      firstName: String,
      lastName: String,
      email: String,
      phoneNumber: String,
      country: String
  ) {
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
                  firstName = firstName,
                  lastName = lastName,
                  email = email,
                  phoneNumber = phoneNumber,
                  country = country,
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

  /**
   * Join an event.
   *
   * @param eventId The id of the event to join.
   * @param userId The id of the user joining the event.
   */
  suspend fun joinEvent(eventId: String, userId: String) {
    try {
      val goMeetUser = getUser(userId)!!
      editUser(goMeetUser.copy(myEvents = goMeetUser.myEvents.plus(eventId)))
    } catch (e: Exception) {
      Log.w(ContentValues.TAG, "Couldn't join the event", e)
    }
  }

  /** TODO */
  suspend fun gotTicket(eventId: String, userId: String) {
    val goMeetUser = getUser(userId)!!
    editUser(goMeetUser.copy(joinedEvents = goMeetUser.joinedEvents.plus(eventId)))
  }

  /**
   * Follow a user.
   *
   * @param uid The uid of the user to follow.
   */
  fun follow(uid: String) {
    CoroutineScope(Dispatchers.IO).launch {
      val senderUid = Firebase.auth.currentUser!!.uid
      val sender = getUser(senderUid)
      val receiver = getUser(uid)
      if (!sender!!.following.contains(uid) && !receiver!!.following.contains(senderUid)) {
        editUser(sender.copy(following = sender.following.plus(uid)))
        editUser(receiver.copy(followers = receiver.followers.plus(senderUid)))
      }
    }
  }

  /**
   * Unfollow a user.
   *
   * @param uid The uid of the user to unfollow.
   */
  fun unfollow(uid: String) {
    CoroutineScope(Dispatchers.IO).launch {
      val senderUid = Firebase.auth.currentUser!!.uid
      val sender = getUser(senderUid)
      val receiver = getUser(uid)
      editUser(sender!!.copy(following = sender.following.minus(uid)))
      editUser(receiver!!.copy(followers = receiver.followers.minus(senderUid)))
    }
  }
}
