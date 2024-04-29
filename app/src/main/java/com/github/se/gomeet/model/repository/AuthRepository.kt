package com.github.se.gomeet.model.repository

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/**
 * This class represents the repository for the authentication of the user. A repository is a class
 * that communicates with the data source.
 *
 * @param fAuth The firebase authentication instance
 */
class AuthRepository(fAuth: FirebaseAuth? = null) {

  private val firebaseAuth = fAuth ?: FirebaseAuth.getInstance()
  val currentUser = firebaseAuth.currentUser

  /**
   * This function checks if the user is signed in
   *
   * @return true if the user is signed in, false otherwise
   */
  fun hasUserSignedIn(): Boolean {
    return currentUser != null
  }

  /**
   * This function gets the user id
   *
   * @return the user id if the user is signed in, null otherwise
   */
  fun getUserId(): String? {
    return currentUser?.uid
  }

  /**
   * This function signs in the user with Google Authentication
   *
   * @return the intent to sign in with Google Authentication if successful, null otherwise
   */
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

  /**
   * This function signs up (register) the user with the Email-Password Authentication
   *
   * @param email The email of the user
   * @param password The password of the user
   * @param onComplete The callback function that is called when the sign up is complete
   */
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

  /**
   * This function signs in (login) the user with the Email-Password Authentication
   *
   * @param email The email of the user
   * @param password The password of the user
   * @param onComplete The callback function that is called when the sign in is complete
   */
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

  /** This function signs out the user */
  fun signOut() {
    firebaseAuth.signOut()
  }
}
