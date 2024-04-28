package com.github.se.gomeet.viewmodel

import com.github.se.gomeet.model.event.EventInviteUsers
import com.github.se.gomeet.model.event.InviteStatus
import com.github.se.gomeet.model.event.UserInvitedToEvents
import com.github.se.gomeet.model.repository.InvitesRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred

// _db argument to be able to pass a mock FirebaseFirestore instance for testing
class EventInviteViewModel {

  private val repository = InvitesRepository(Firebase.firestore)

  suspend fun getUsersInvitedToEvent(eventId: String): UserInvitedToEvents? {
    return try {
      val userInvitedToEvents = CompletableDeferred<UserInvitedToEvents?>()
      repository.getEventInvites(eventId) { t -> userInvitedToEvents.complete(t) }
      userInvitedToEvents.await()
    } catch (e: Exception) {
      null
    }
  }

  suspend fun getEventsUserHasBeenInvitedTo(userId: String): EventInviteUsers? {
    return try {
      val eventInviteUsers = CompletableDeferred<EventInviteUsers?>()
      repository.getUserInvites(userId) { t -> eventInviteUsers.complete(t) }
      eventInviteUsers.await()
    } catch (e: Exception) {
      null
    }
  }

  /*Return true if invite has been successfully sent*/
  fun sendInviteToUser(userID: String, eventId: String): Boolean? {
    return try {
      repository.sendInvite(userID, eventId)
    } catch (e: Exception) {
      null
    }
  }

  fun userAcceptsInvite(userId: String, eventId: String) {
    repository.updateInvite(userId, eventId, InviteStatus.ACCEPTED)
  }

  fun userRefusesInvite(userId: String, eventId: String) {
    repository.updateInvite(userId, eventId, InviteStatus.REFUSED)
  }

  fun removeInvite(userId: String, eventId: String) {
    repository.removeInvite(eventId, userId)
  }
}
