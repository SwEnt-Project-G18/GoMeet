package com.github.se.gomeet.model.repository

import android.util.Log
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.EventInvite
import com.github.se.gomeet.model.event.InviteStatus
import com.github.se.gomeet.model.event.UsersInvited
import com.github.se.gomeet.model.event.location.Location
import com.github.se.gomeet.model.user.GoMeetUser
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import com.google.firebase.ktx.Firebase
class InvitesRepository (db: FirebaseFirestore) {
    private val localInvitedTo: MutableList<Event> = mutableListOf()
    init {
        startListeningForInvites()
    }

    companion object {
        private const val TAG = "FirebaseConnection"
        private const val USER_INVITES_COLLECTION = "UserInvites"
        private const val EVENT_INVITES_COLLECTION = "EventInvites"

    }

    fun getUserInvites(uid: String, callback: (List<Event>?) -> Unit) {

    }

    fun getEventInvites(id: String, callback: (List<GoMeetUser>?) -> Unit) {

    }

    fun sendInvite(eventID: String, toUserID: String) {

    }

    fun removeInvite(eventID: String, toUserID: String) {

    }

    private fun EventInvite.toMap(): Map<String, Any?> {
        return mapOf(
            "e" to e,
            "usersInvited" to usersInvited
        )
    }

    private fun Map<String, Any>.toEventInvite(id: String? = null): EventInvite {
        return EventInvite(
            e = id ?: this["e"] as? String ?: "",
            usersInvited = this["usersInvited"] as? List<Pair<String, InviteStatus>> ?: emptyList()
        )
    }

    private fun UsersInvited.toMap(): Map<String, Any?> {
        return mapOf(
            "u" to u,
            "invitedToEvents" to invitedToEvents
        )
    }

    private fun Map<String, Any>.toUsersInvited(id: String? = null): UsersInvited {
        return UsersInvited(
            u = id ?: this["u"] as? String ?: "",
            invitedToEvents = this["invitedToEvents"] as? List<Pair<String, InviteStatus>> ?: emptyList()
        )
    }

    private fun startListeningForInvites() {

    }
}