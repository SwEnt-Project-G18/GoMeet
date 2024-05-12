package com.github.se.gomeet.viewmodel

import com.github.se.gomeet.model.event.EventInviteUsers
import com.github.se.gomeet.model.event.InviteStatus
import com.github.se.gomeet.model.event.UserInvitedToEvents
import com.github.se.gomeet.model.repository.InvitesRepository
import kotlinx.coroutines.CompletableDeferred

/**
 * ViewModel for event invitation logic. The viewModel is responsible for handling the logic that
 * comes from the UI and the repository.
 */
class EventInviteViewModel(invitesRepository: InvitesRepository) {

  private val repository = invitesRepository

  /**
   * Get the users invited to an event.
   *
   * @param eventId the id of the event
   * @return the users invited to the event
   */
  suspend fun getUsersInvitedToEvent(eventId: String): EventInviteUsers? {
    return try {
      val eventInviteUsers = CompletableDeferred<EventInviteUsers?>()
      repository.getUsersInvitedToThisEvent(eventId) { t -> eventInviteUsers.complete(t) }
      eventInviteUsers.await()
    } catch (e: Exception) {
      null
    }
  }

  /**
   * Get the events a user has been invited to.
   *
   * @param userId the id of the user
   * @return the events the user has been invited to
   */
  suspend fun getEventsUserHasBeenInvitedTo(userId: String): UserInvitedToEvents? {
    return try {
      val userInvitedToEvents = CompletableDeferred<UserInvitedToEvents?>()
      repository.getEventsUserHasBeenInvitedTo(userId) { t -> userInvitedToEvents.complete(t) }
      userInvitedToEvents.await()
    } catch (e: Exception) {
      null
    }
  }

  /*Return true if invite has been successfully sent*/
  /**
   * Send an invite to a user.
   *
   * @param userID the id of the user to send the invite to
   * @param eventId the id of the event to invite the user to
   * @return true if the invite has been successfully sent
   */
  fun sendInviteToUser(userID: String, eventId: String): Boolean? {
    return try {
      repository.sendInvite(eventID = eventId, toUserID = userID)
    } catch (e: Exception) {
      null
    }
  }

  /**
   * Update the status of an invitation to ACCEPTED.
   *
   * @param userId the id of the user
   * @param eventId the id of the event
   * @return true if the status has been successfully updated to ACCEPTED
   */
  fun userAcceptsInvite(userId: String, eventId: String) {
    repository.updateInvite(userId, eventId, InviteStatus.ACCEPTED)
  }

  /**
   * Update the status of an invitation to REFUSED.
   *
   * @param userId the id of the user
   * @param eventId the id of the event
   * @return true if the status has been successfully updated to REFUSED
   */
  fun userRefusesInvite(userId: String, eventId: String) {
    repository.updateInvite(userId, eventId, InviteStatus.REFUSED)
  }

  /**
   * Remove an invitation.
   *
   * @param userId the id of the user
   * @param eventId the id of the event
   */
  fun removeInvite(userId: String, eventId: String) {
    repository.removeInvite(eventId, userId)
  }

  fun addEventInviteUsers(e: EventInviteUsers) {
    e.usersInvited.forEach { repository.sendInvite(e.event, it.key) }
  }
}
