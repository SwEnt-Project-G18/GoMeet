package com.github.se.gomeet.model.repository

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository {

  private val firebaseAuth = FirebaseAuth.getInstance()
  val currentUser = firebaseAuth.currentUser

  fun hasUserSignedIn(): Boolean {
    return currentUser != null
  }

  fun getUserId(): String? {
    return currentUser?.uid
  }

  suspend fun signInWithGoogle(): Intent? {
    return try {
      val signInIntent =
          AuthUI.getInstance()
              .createSignInIntentBuilder()
              .setAvailableProviders(listOf(AuthUI.IdpConfig.GoogleBuilder().build()))
              .build()
      signInIntent
    } catch (e: Exception) {
      Log.e(TAG, "signInWithGoogle:failure", e)
      null
    }
  }

  suspend fun signUpWithEmailPassword(
      email: String,
      password: String,
      onComplete: (Boolean) -> Unit
  ) {
    try {
      firebaseAuth.createUserWithEmailAndPassword(email, password).await()
      Log.d(TAG, "signUpWithEmail:success")
      onComplete(true)
    } catch (e: Exception) {
      Log.w(TAG, "signUpWithEmail:failure", e)
      onComplete(false)
    }
  }

  suspend fun signInWithEmailPassword(
      email: String,
      password: String,
      onComplete: (Boolean) -> Unit
  ) {
    try {
      // Attempt to sign in and wait for the task to complete
      firebaseAuth.signInWithEmailAndPassword(email, password).await()
      // If the await() completes without throwing an exception, sign-in was successful
      Log.d(TAG, "signInWithEmail:success")
      onComplete(true)
    } catch (e: Exception) {
      // If await() throws an exception, sign-in failed
      Log.w(TAG, "signInWithEmail:failure", e)
      onComplete(false)
    }
  }

  fun signOut() {
    firebaseAuth.signOut()
  }
}
