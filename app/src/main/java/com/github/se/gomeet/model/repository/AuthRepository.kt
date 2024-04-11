package com.github.se.gomeet.model.repository

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository(fAuth: FirebaseAuth? = null) {

  private val firebaseAuth = fAuth ?: FirebaseAuth.getInstance()
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
    firebaseAuth
        .createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            Log.d(TAG, "signUpWithEmail:success")
            onComplete.invoke(true)
          } else {
            Log.w(TAG, "signUpWithEmail:failure", task.exception)
            onComplete.invoke(false)
          }
        }
        .await()
  }

  suspend fun signInWithEmailPassword(
      email: String,
      password: String,
      onComplete: (Boolean) -> Unit
  ) {
    firebaseAuth
        .signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            Log.d(TAG, "signInWithEmail:success")
            onComplete.invoke(true)
          } else {
            Log.w(TAG, "signInWithEmail:failure", task.exception)
            onComplete.invoke(false)
          }
        }
        .await()
  }

  fun signOut() {
    firebaseAuth.signOut()
  }
}
