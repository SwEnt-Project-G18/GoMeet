package com.github.se.gomeet

import android.util.Log
import com.github.se.gomeet.model.user.GoMeetUser
import com.google.firebase.firestore.FirebaseFirestore

class UserFirebaseConnection(private val db: FirebaseFirestore) {
  companion object {
    private const val TAG = "FirebaseConnection"
    private const val USERS_COLLECTION = "users"
  }

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

  fun addUser(user: GoMeetUser) {
    db.collection(USERS_COLLECTION)
        .document(user.uid)
        .set(user.toMap())
        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
  }

  fun updateUser(user: GoMeetUser) {
    val documentRef = db.collection(USERS_COLLECTION).document(user.uid)
    documentRef
        .update(user.toMap())
        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
  }

  fun removeUser(user: GoMeetUser) {
    db.collection(USERS_COLLECTION)
        .document(user.uid)
        .delete()
        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
  }

  private fun GoMeetUser.toMap(): Map<String, Any?> {
    return mapOf(
        "uid" to uid,
        "username" to username,
        "following" to following,
        "followers" to followers,
        "pendingRequests" to pendingRequests)
  }

  private fun Map<String, Any>.fromMap(id: String): GoMeetUser {
    return GoMeetUser(
        uid = id,
        username = this["username"] as String,
        following = this["following"] as List<String>,
        followers = this["followers"] as List<String>,
        pendingRequests = this["pendingRequests"] as List<String>)
  }
}
