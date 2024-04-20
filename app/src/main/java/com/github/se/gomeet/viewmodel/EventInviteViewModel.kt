package com.github.se.gomeet.viewmodel

import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// _db argument to be able to pass a mock FirebaseFirestore instance for testing
class EventInviteViewModel(_db: FirebaseFirestore? = null) {

    private val db = _db ?: Firebase.firestore
    // Store invites as list of strings (references to the event documents)
    private val eventInvites: MutableList<String> = mutableListOf()

    // Constructor
    init{
        startListeningForEventInvites()
    }
    fun getEventInvite(eventId: String) {
        db.collection("events").document(eventId).get()
    }



    private fun startListeningForEventInvites() {
        db.collection("events").addSnapshotListener { snapshot, e ->
            if (e != null) {
                // Handle error
                Log.w("EventRepository", "Listen failed.", e)
                return@addSnapshotListener
            }

            for (docChange in snapshot?.documentChanges!!) {

                val invite = docChange.document.data["invite"] as String

                when (docChange.type) {
                    DocumentChange.Type.ADDED -> {
                        eventInvites.add(invite)
                    }
                    DocumentChange.Type.MODIFIED -> {}
                    DocumentChange.Type.REMOVED -> {
                        eventInvites.removeIf { it == invite }
                    }
                }
            }
        }
    }
}