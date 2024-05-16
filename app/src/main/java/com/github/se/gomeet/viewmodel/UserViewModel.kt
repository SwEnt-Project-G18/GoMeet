package com.github.se.gomeet.viewmodel

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gomeet.model.event.Invitation
import com.github.se.gomeet.model.event.InviteStatus
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.model.user.GoMeetUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel for the user. The viewModel is responsible for handling the logic that comes from the
 * UI and the repository.
 */
class UserViewModel(userRepository: UserRepository) : ViewModel() {
  private val currentUser = mutableStateOf<GoMeetUser?>(null)
  private val repository = userRepository

  /**
   * Create a new user if the user is new.
   *
   * @param uid the user id
   * @param username the username
   * @param firstName the first name
   * @param lastName the last name
   * @param email the email
   * @param phoneNumber the phone number
   * @param country the country
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
                  pendingRequests = emptySet(),
                  firstName = firstName,
                  lastName = lastName,
                  email = email,
                  phoneNumber = phoneNumber,
                  country = country,
                  joinedEvents = emptyList(),
                  myEvents = emptyList(),
                  myFavorites = emptyList(),
                  tags = emptyList())
          currentUser.value = user
          repository.addUser(user)
        } catch (e: Exception) {
          Log.w(ContentValues.TAG, "Error adding user", e)
        }
      }
    }
  }

  suspend fun getFollowers(uid: String): List<GoMeetUser> {
    val followers = mutableListOf<GoMeetUser>()
    repository.getAllUsers { users ->
      for (user in users) {
        if (user.uid != uid && user.following.contains(uid)) {
          followers.add(user)
        }
      }
    }
    return followers
  }

  fun uploadImageAndGetUrl(
      userId: String,
      imageUri: Uri,
      onSuccess: (String) -> Unit,
      onError: (Exception) -> Unit
  ) {
    viewModelScope.launch {
      try {
        val imageUrl = repository.uploadUserProfileImageAndGetUrl(userId, imageUri)
        onSuccess(imageUrl)
      } catch (e: Exception) {
        onError(e)
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
      repository.getUser(uid) { t -> event.complete(t) }
      event.await()
    } catch (e: Exception) {
      null
    }
  }

  /**
   * Get all users of the app
   *
   * @return a list of all users
   */
  suspend fun getAllUsers(): List<GoMeetUser>? {
    return try {
      val users = CompletableDeferred<List<GoMeetUser>?>()
      repository.getAllUsers { t -> users.complete(t) }
      users.await()
    } catch (e: Exception) {
      emptyList()
    }
  }

  /**
   * Edit the user globally.
   *
   * @param user the user to edit
   */
  fun editUser(user: GoMeetUser) {
    repository.updateUser(user)
  }

  /**
   * Delete the user.
   *
   * @param uid the user id
   */
  fun deleteUser(uid: String) {
    repository.removeUser(uid)
  }

  /**
   * Join an event.
   *
   * @param eventId The id of the event to join.
   * @param userId The id of the user joining the event.
   */
  suspend fun joinEvent(eventId: String, userId: String) {
    val possibleInvitation =
        getUser(userId)!!.pendingRequests.find {
          it.eventId == eventId && it.status == InviteStatus.PENDING
        }
    try {
      val goMeetUser = getUser(userId)!!
      if (possibleInvitation != null) {
        editUser(
            goMeetUser.copy(pendingRequests = goMeetUser.pendingRequests.minus(possibleInvitation)))
      }
      editUser(goMeetUser.copy(joinedEvents = goMeetUser.joinedEvents.plus(eventId)))
    } catch (e: Exception) {
      Log.w(ContentValues.TAG, "Couldn't join the event", e)
    }
  }

  /**
   * User creates an event and adds it to their list of myEvents. It is used when a user creates an
   * event.
   *
   * @param eventId The id of the event to create.
   * @param userId The id of the user creating the event.
   */
  suspend fun userCreatesEvent(eventId: String, userId: String) {
    try {
      val goMeetUser = getUser(userId)!!
      editUser(goMeetUser.copy(myEvents = goMeetUser.myEvents.plus(eventId)))
    } catch (e: Exception) {
      Log.w(ContentValues.TAG, "Couldn't create the event", e)
    }
  }

  /**
   * The user receives an invitation and adds it to their list of pendingRequests. Note that this
   * function should be called in the same time as the equivalent function in the EventViewModel.
   *
   * @param eventId The id of the event to invite the user to.
   * @param userId The id of the user to invite.
   */
  suspend fun gotInvitation(eventId: String, userId: String) {
    val possibleInvitation =
        getUser(userId)!!.pendingRequests.find {
          it.eventId == eventId && it.status == InviteStatus.PENDING
        }

    try {
      val goMeetUser = getUser(userId)!!
      if (goMeetUser.joinedEvents.contains(eventId) ||
          goMeetUser.pendingRequests.contains(possibleInvitation)) {
        Log.w(
            (ContentValues.TAG),
            "User already joined this event or has a pending request for this event")
        return
      }
      editUser(
          goMeetUser.copy(
              pendingRequests =
                  goMeetUser.pendingRequests.plus(Invitation(eventId, InviteStatus.PENDING))))
    } catch (e: Exception) {
      Log.w(ContentValues.TAG, "Couldn't get the invitation", e)
    }
  }

  /**
   * The user gets kicked from an event and removes it from their list of joinedEvents. Note that
   * this function should be called in the same time as the equivalent function in the
   * EventViewModel.
   *
   * @param eventId The id of the event to kick the user from.
   * @param userId The id of the user to kick.
   */
  suspend fun gotKickedFromEvent(eventId: String, userId: String) {
    try {
      val goMeetUser = getUser(userId)!!
      editUser(goMeetUser.copy(joinedEvents = goMeetUser.joinedEvents.minus(eventId)))
    } catch (e: Exception) {
      Log.w(ContentValues.TAG, "Couldn't get kicked from the event", e)
    }
  }

  /**
   * The user got his invitation canceled and is removed from his list of pendingRequests. Note that
   * this function should be called in the same time as the equivalent function in the
   * EventViewModel.
   *
   * @param eventId The id of the event to cancel the invitation for.
   * @param userId The id of the user to cancel the invitation for.
   */
  suspend fun invitationCanceled(eventId: String, userId: String) {
    val possibleInvitation =
        getUser(userId)!!.pendingRequests.find {
          it.eventId == eventId && it.status == InviteStatus.PENDING
        }
    try {
      val goMeetUser = getUser(userId)!!
      if (possibleInvitation != null) {
        editUser(
            goMeetUser.copy(pendingRequests = goMeetUser.pendingRequests.minus(possibleInvitation)))
      }
    } catch (e: Exception) {
      Log.w(ContentValues.TAG, "Couldn't cancel the invitation", e)
    }
  }

  /**
   * The user accepts an invitation and adds the event to his list of joinedEvents and removes it
   * from his list of pendingRequests. Note that this function should be called in the same time as
   * the equivalent function in the EventViewModel.
   *
   * @param eventId The id of the event to accept the invitation for.
   * @param userId The id of the user to accept the invitation for.
   */
  fun userAcceptsInvitation(eventId: String, userId: String) {
    CoroutineScope(Dispatchers.IO).launch {
      val goMeetUser = getUser(userId)!!
      val possibleInvitation =
          goMeetUser.pendingRequests.find {
            it.eventId == eventId && it.status == InviteStatus.PENDING
          }

      if (possibleInvitation != null) {
        editUser(
            goMeetUser.copy(
                pendingRequests = goMeetUser.pendingRequests.minus(possibleInvitation),
                joinedEvents = goMeetUser.joinedEvents.plus(eventId)))
      } else {
        Log.w(ContentValues.TAG, "Couldn't accept the invitation")
      }
    }
  }

  /**
   * The user refuses an invitation and removes it from his list of pendingRequests. Note that this
   * function should be called in the same time as the equivalent function in the EventViewModel.
   *
   * @param eventId The id of the event to refuse the invitation for.
   * @param userId The id of the user to refuse the invitation for.
   */
  fun userRefusesInvitation(eventId: String, userId: String) {
      CoroutineScope(Dispatchers.IO).launch {
          val goMeetUser = getUser(userId)!!
          val possibleInvitation = goMeetUser.pendingRequests.find {
              it.eventId == eventId && it.status == InviteStatus.PENDING
          }

          if (possibleInvitation != null) {
              val updatedPendingRequests = goMeetUser.pendingRequests.map {
                  if (it.eventId == eventId) {
                      it.copy(status = InviteStatus.REFUSED)
                  } else {
                      it
                  }
              }.toSet()

              editUser(goMeetUser.copy(pendingRequests = updatedPendingRequests))
          } else {
              Log.w(ContentValues.TAG, "Couldn't refuse the invitation: Invitation not found")
          }
      }
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

  /**
   * Get the username of a user.
   *
   * @param uid The uid of the user.
   * @return The username of the user.
   */
  suspend fun getUsername(uid: String): String? {
    return try {
      val user = getUser(uid)
      user?.username
    } catch (e: Exception) {
      Log.e("GetUserError", "Error retrieving user: ${e.message}")
      null
    }
  }
}
