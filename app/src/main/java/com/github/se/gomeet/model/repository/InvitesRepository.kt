package com.github.se.gomeet.model.repository

import android.util.Log
import com.github.se.gomeet.model.event.EventInviteUsers
import com.github.se.gomeet.model.event.InviteStatus
import com.github.se.gomeet.model.event.UserInvitedToEvents
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

/**
 * This class represents the repository for the invites.
 * A repository is a class that communicates with the data source.
 *
 * @param db The database firebase instance
 */
class InvitesRepository(private val db: FirebaseFirestore) {
  private val localInvitedTo: MutableList<UserInvitedToEvents> = mutableListOf()

    /**
     * This function initializes the repository by starting to listen for invites
     */
  init {
    startListeningForInvites()
  }

    /**
     * This companion object contains the constants for the repository
     */
  companion object {
    const val TAG = "FirebaseConnection"
    private const val USER_INVITES_COLLECTION = "UserInvites"
    private const val EVENT_INVITES_COLLECTION = "EventInvites"
  }

    /**
     * This function retrieves all the events the user has been invited to
     *
     * @param uid The user ID
     * @param callback The callback function to be called when the events are retrieved
     *
     * @return the events the user has been invited to if they exist, null otherwise
     */
  fun getUserInvites(uid: String, callback: (EventInviteUsers?) -> Unit) {
    db.collection(USER_INVITES_COLLECTION)
        .document(uid)
        .get()
        .addOnSuccessListener { document ->
          if (document != null && document.exists()) {
            val eventInvite = document.data!!.toEventInviteUsers(uid)
            callback(eventInvite)
          } else {
            Log.d(TAG, "No such document")
            callback(null)
          }
        }
        .addOnFailureListener { exception ->
          Log.d(TAG, "get failed with ", exception)
          callback(null)
        }
  }

    /**
     * This function retrieves all the users invited to an event
     *
     * @param id The event ID
     * @param callback The callback function to be called when the users are retrieved
     *
     * @return the users invited to the event if they exist, null otherwise
     */
  fun getEventInvites(id: String, callback: (UserInvitedToEvents?) -> Unit) {
    db.collection(USER_INVITES_COLLECTION)
        .document(id)
        .get()
        .addOnSuccessListener { document ->
          if (document != null && document.exists()) {
            val usersInvited = document.data!!.toUsersInvitedToEvents(id)
            callback(usersInvited)
          } else {
            Log.d(TAG, "No such document")
            callback(null)
          }
        }
        .addOnFailureListener { exception ->
          Log.d(TAG, "get failed with ", exception)
          callback(null)
        }
  }

    /**
     * This function updates the event invites
     *
     * @param eventID The event ID
     * @param userID The user ID
     * @param status The status of the invite
     * @param success The callback function to be called when the event invites are updated
     *
     * @return true if the event invites are updated, false otherwise
     */
  private fun updateEventInvites(
      eventID: String,
      userID: String,
      status: InviteStatus,
      success: (Boolean) -> Unit
  ) {
    val eventInviteRef = db.collection(EVENT_INVITES_COLLECTION).document(eventID)
    db.runTransaction { transaction ->
          val snapshot = transaction.get(eventInviteRef)
          val currentUsers =
              if (snapshot.exists()) {
                snapshot.toObject(EventInviteUsers::class.java)?.usersInvited?.toMutableList()
                    ?: mutableListOf()
              } else {
                mutableListOf()
              }
          currentUsers.add(Pair(userID, status))
          transaction.set(eventInviteRef, EventInviteUsers(eventID, currentUsers))
        }
        .addOnSuccessListener {
          Log.d(TAG, "Event invite updated successfully")
          success(true)
        }
        .addOnFailureListener { e ->
          Log.d(TAG, "Error updating event invite", e)
          success(false)
        }
  }

    /**
     * This function updates the user invites
     *
     * @param userID The user ID
     * @param eventID The event ID
     * @param status The status of the invite
     * @param success The callback function to be called when the user invites are updated
     *
     * @return true if the user invites are updated, false otherwise
     */
  private fun updateUserInvites(
      userID: String,
      eventID: String,
      status: InviteStatus,
      success: (Boolean) -> Unit
  ) {
    val userInviteRef = db.collection(USER_INVITES_COLLECTION).document(userID)
    db.runTransaction { transaction ->
          val snapshot = transaction.get(userInviteRef)
          val currentEvents =
              if (snapshot.exists()) {
                snapshot.toObject(UserInvitedToEvents::class.java)?.invitedToEvents?.toMutableList()
                    ?: mutableListOf()
              } else {
                mutableListOf()
              }
          currentEvents.add(Pair(eventID, status))
          transaction.set(userInviteRef, UserInvitedToEvents(userID, currentEvents))
        }
        .addOnSuccessListener {
          Log.d(TAG, "User invite updated successfully")
          success(true)
        }
        .addOnFailureListener { e ->
          Log.d(TAG, "Error updating user invite", e)
          success(false)
        }
  }

    /**
     * This function updates the event invites and user invites with the status PENDING
     *
     * @param eventID The event ID
     * @param toUserID The user ID
     *
     * @return true if the invite is sent, false otherwise
     */
  fun sendInvite(eventID: String, toUserID: String): Boolean {
    var a = false
    updateEventInvites(eventID, toUserID, InviteStatus.PENDING) { a = it }
    updateUserInvites(toUserID, eventID, InviteStatus.PENDING) { a = a && it }
    return a
  }

    /**
     * This function updates the event invites and user invites with the given status
     *
     * @param eventID The event ID
     * @param toUserID The user ID
     * @param status The status of the invite
     *
     * @return true if the invite is updated, false otherwise
     */
  fun updateInvite(eventID: String, toUserID: String, status: InviteStatus): Boolean {
    var a = false
    updateEventInvites(eventID, toUserID, status) { a = it }
    updateUserInvites(toUserID, eventID, status) { a = a && it }
    return a
  }

    /**
     * This function removes the event invite and user invite
     *
     * @param eventID The event ID
     * @param toUserID The user ID
     */
  fun removeInvite(eventID: String, toUserID: String) {
    val eventInviteRef = db.collection(EVENT_INVITES_COLLECTION).document(eventID)
    val userInviteRef = db.collection(USER_INVITES_COLLECTION).document(toUserID)

    db.runTransaction { transaction ->
          // Remove user from event invites
          val eventSnapshot = transaction.get(eventInviteRef)
          val updatedEventUsers =
              eventSnapshot.data!!
                  .toEventInviteUsers(eventID)
                  .usersInvited
                  .filterNot { it.first == toUserID }
                  .toMutableList()
          transaction.set(eventInviteRef, EventInviteUsers(eventID, updatedEventUsers))

          // Remove event from user's invites
          val userSnapshot = transaction.get(userInviteRef)
          val updatedUserEvents =
              userSnapshot.data!!
                  .toUsersInvitedToEvents(toUserID)
                  .invitedToEvents
                  .filterNot { it.first == eventID }
                  .toMutableList()
          transaction.set(userInviteRef, UserInvitedToEvents(toUserID, updatedUserEvents))
        }
        .addOnSuccessListener { Log.d(TAG, "Transaction successful: both invites removed") }
        .addOnFailureListener { e -> Log.d(TAG, "Transaction failed", e) }
  }

    /**
     * Maps the events and users to their respective fields in the database
     */
  private fun EventInviteUsers.toMap(): Map<String, Any?> {
    return mapOf("e" to event, "usersInvited" to usersInvited)
  }

    /**
     * Maps the fields in the database to the events and users
     *
     * @param id The event ID
     */
  private fun Map<String, Any>.toEventInviteUsers(id: String? = null): EventInviteUsers {
    return EventInviteUsers(
        event = id ?: this["e"] as? String ?: "",
        usersInvited =
            this["usersInvited"] as? MutableList<Pair<String, InviteStatus>> ?: mutableListOf())
  }

    /**
     * Maps the users and events to their respective fields in the database
     */
  private fun UserInvitedToEvents.toMap(): Map<String, Any?> {
    return mapOf("u" to user, "invitedToEvents" to invitedToEvents)
  }

    /**
     * Maps the fields in the database to the users and events
     *
     * @param id The user ID
     */
  private fun Map<String, Any>.toUsersInvitedToEvents(id: String? = null): UserInvitedToEvents {
    return UserInvitedToEvents(
        user = id ?: this["u"] as? String ?: "",
        invitedToEvents =
            this["invitedToEvents"] as? MutableList<Pair<String, InviteStatus>> ?: mutableListOf())
  }

    /**
     * This function starts listening for invites
     *
     * @return the list of users invited to events
     */
  private fun startListeningForInvites() {
    db.collection("events").addSnapshotListener { snapshot, e ->
      if (e != null) {
        // Handle error
        Log.w("EventRepository", "Listen failed.", e)
        return@addSnapshotListener
      }

      for (docChange in snapshot?.documentChanges!!) {

        val userInvitedToEvents = docChange.document.data.toUsersInvitedToEvents()
        when (docChange.type) {
          DocumentChange.Type.ADDED -> {
            localInvitedTo.add(userInvitedToEvents)
          }
          DocumentChange.Type.MODIFIED -> {
            localInvitedTo
                .find { it == userInvitedToEvents }
                ?.let { localInvitedTo[localInvitedTo.indexOf(it)] = userInvitedToEvents }
          }
          DocumentChange.Type.REMOVED -> {
            localInvitedTo.removeIf { it == userInvitedToEvents }
          }
        }
      }
    }
  }
}
