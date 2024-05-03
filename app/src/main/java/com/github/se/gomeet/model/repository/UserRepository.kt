package com.github.se.gomeet.model.repository

import android.util.Log
import com.github.se.gomeet.model.user.GoMeetUser
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Class that connects to the Firebase Firestore database to get, add, update and remove users.
 *
 * @param db the Firestore database
 */
class UserRepository(private val db: FirebaseFirestore) {
  /**
   * Companion object for the UserFirebaseConnection class. Contains the constants for the class.
   */
  companion object {
    private const val TAG = "FirebaseConnection"
    private const val USERS_COLLECTION = "users"
  }

  /**
   * This function retrieves all users from the database
   *
   * @param callback The callback function to be called when the users are retrieved
   */
  fun getAllUsers(callback: (List<GoMeetUser>) -> Unit) {
    db.collection(USERS_COLLECTION)
        .get()
        .addOnSuccessListener { querySnapshot ->
          val userList = mutableListOf<GoMeetUser>()
          for (document in querySnapshot.documents) {
            val user = document.data?.fromMap(document.id)
            if (user != null) {
              userList.add(user)
            }
          }
        }
        .addOnFailureListener { exception ->
          Log.d(TAG, "Error getting documents: ", exception)
          callback(emptyList())
        }
  }

  /**
   * Get the user with its id.
   *
   * @param uid the user id
   * @param callback the callback function
   */
  fun getUser(uid: String, callback: (GoMeetUser?) -> Unit) {
    db.collection(USERS_COLLECTION)
        .document(uid)
        .get()
        .addOnSuccessListener { document ->
          if (document != null && document.exists()) {
            val user = document.data!!.fromMap(uid)
            callback(user)
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
   * Add a user to the database.
   *
   * @param user the user to add
   */
  fun addUser(user: GoMeetUser) {
    db.collection(USERS_COLLECTION)
        .document(user.uid)
        .set(user.toMap())
        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
  }

  /**
   * Update a user in the database.
   *
   * @param user the user to update
   */
  fun updateUser(user: GoMeetUser) {
    val documentRef = db.collection(USERS_COLLECTION).document(user.uid)
    documentRef
        .update(user.toMap())
        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
  }

  /**
   * Remove a user from the database.
   *
   * @param uid the user id
   */
  fun removeUser(uid: String) {
    db.collection(USERS_COLLECTION)
        .document(uid)
        .delete()
        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
  }

  /**
   * Convert a GoMeetUser to a map.
   *
   * @return the map
   */
  private fun GoMeetUser.toMap(): Map<String, Any?> {
    return mapOf(
        "uid" to uid,
        "username" to username,
        "following" to following,
        "followers" to followers,
        "pendingRequests" to pendingRequests,
        "firstName" to firstName,
        "lastName" to lastName,
        "email" to email,
        "phoneNumber" to phoneNumber,
        "country" to country,
        "myTickets" to joinedEvents,
        "myEvents" to myEvents,
        "myFavorites" to myFavorites)
  }

  /**
   * Convert a map to a GoMeetUser.
   *
   * @param id the user id
   * @return the GoMeetUser
   */
  private fun Map<String, Any>.fromMap(id: String): GoMeetUser {
    return GoMeetUser(
        uid = id,
        username = this["username"] as String,
        following = this["following"] as List<String>,
        followers = this["followers"] as List<String>,
        pendingRequests = this["pendingRequests"] as List<String>,
        firstName = this["firstName"] as? String ?: "",
        lastName = this["lastName"] as? String ?: "",
        email = this["email"] as? String ?: "",
        phoneNumber = this["phoneNumber"] as? String ?: "",
        country = this["country"] as? String ?: "",
        joinedEvents = this["myTickets"] as List<String>,
        myEvents = this["myEvents"] as List<String>,
        myFavorites = this["myFavorites"] as List<String>)
  }
}
