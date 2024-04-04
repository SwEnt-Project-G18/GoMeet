package com.github.se.gomeet.model.repository

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

class AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun signInWithGoogle(): Intent? {
        return try {
            val signInIntent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(listOf(AuthUI.IdpConfig.GoogleBuilder().build()))
                .build()
            signInIntent
        } catch (e: Exception) {
            Log.e(TAG, "signInWithGoogle:failure", e)
            null
        }
    }

    suspend fun signInWithEmailPassword(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    firebaseAuth.currentUser?.uid
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                }
            }
    }

    suspend fun signUpWithEmailPassword(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    firebaseAuth.currentUser?.uid
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                }
            }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}