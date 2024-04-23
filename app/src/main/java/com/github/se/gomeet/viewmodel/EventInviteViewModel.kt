package com.github.se.gomeet.viewmodel

import android.util.Log
import com.github.se.gomeet.model.repository.InvitesRepository
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// _db argument to be able to pass a mock FirebaseFirestore instance for testing
class EventInviteViewModel() {

    private val db = InvitesRepository(Firebase.firestore)

    // Constructor
    init{
        startListeningForEventInvites()
    }
    fun getEventInvite(eventId: String) {

    }



    private fun startListeningForEventInvites() {

    }
}