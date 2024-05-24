package com.github.se.gomeet.model.repository

import android.net.Uri
import android.util.Log
import com.github.se.gomeet.model.event.Invitation
import com.github.se.gomeet.model.event.InviteStatus
import com.github.se.gomeet.model.user.*
import com.github.se.gomeet.model.user.GoMeetUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

/** Class that connects to the Firebase Firestore database to get, add, update and remove users. */
class UserRepository private constructor() {

  /** This companion object contains the functions in the repository */
  companion object {
    private const val TAG = "UserRepository"
    private const val USERS_COLLECTION = "users"

    /**
     * This function retrieves all users from the database
     *
     * @param callback The callback function to be called when the users are retrieved
     */
    fun getAllUsers(callback: (List<GoMeetUser>) -> Unit) {
      Firebase.firestore
          .collection(USERS_COLLECTION)
          .get()
          .addOnSuccessListener { querySnapshot ->
            val userList = mutableListOf<GoMeetUser>()
            for (document in querySnapshot.documents) {
              val user = document.data?.toUser(document.id)
              if (user != null) {
                userList.add(user)
              }
            }
            callback(userList)
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
      Firebase.firestore
          .collection(USERS_COLLECTION)
          .document(uid)
          .get()
          .addOnSuccessListener { document ->
            if (document != null && document.exists()) {
              val user = document.data!!.toUser(uid)
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
      Firebase.firestore
          .collection(USERS_COLLECTION)
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
      val documentRef = Firebase.firestore.collection(USERS_COLLECTION).document(user.uid)
      documentRef
          .update(user.toMap())
          .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
          .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
    }

    /**
     * Upload a user profile image to Firebase Storage and return the download URL.
     *
     * @param userId the user ID
     * @param imageUri the URI of the image to upload
     * @return the download URL of the uploaded image
     */
    suspend fun uploadUserProfileImageAndGetUrl(userId: String, imageUri: Uri): String {
      val storageReference = FirebaseStorage.getInstance().reference
      val imageRef = storageReference.child("user_images/$userId/${imageUri.lastPathSegment}")

      // Upload the file and await the completion
      val uploadTaskSnapshot = imageRef.putFile(imageUri).await()

      // Retrieve and return the download URL
      return uploadTaskSnapshot.metadata?.reference?.downloadUrl?.await()?.toString()
          ?: throw Exception("Failed to upload image and retrieve URL")
    }

    /**
     * Remove a user from the database.
     *
     * @param uid the user id
     */
    fun removeUser(uid: String) {
      Firebase.firestore
          .collection(USERS_COLLECTION)
          .document(uid)
          .delete()
          .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
          .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }

  suspend fun updateUserRating(userID: String, newRating: Long, oldRating: Long) {

      if(oldRating == newRating || newRating < 0 || newRating > 5 || oldRating < 0 || oldRating > 5) return

      val oldUserRatingMap = Firebase.firestore.collection(USERS_COLLECTION).document(userID).get().await().get(RATING) as Map<String, Long>
      val oldUserRating = Pair(oldUserRatingMap["first"], oldUserRatingMap["second"])
      val addOrRemoveCount = if(oldRating == 0L) 1L else if(newRating == 0L && oldUserRating.second!! > 0) -1L else 0L
      val newUserRating = Pair(oldUserRating.first!! + newRating - oldRating, oldUserRating.second!! + addOrRemoveCount)
      Firebase.firestore.collection(USERS_COLLECTION).document(userID).update(RATING, newUserRating)
          .addOnSuccessListener { Log.d(TAG, "Successfully updated rating of user ${userID}") }
          .addOnFailureListener { e -> Log.w(TAG, "Error updating rating of user ${userID}", e) }
      Log.d(TAG, "Updating rating of user($oldRating to $newRating) ${userID} from ${oldUserRating} to ${newUserRating}")
  }

    /**
     * Convert a GoMeetUser to a map.
     *
     * @return the map
     */
    private fun GoMeetUser.toMap(): Map<String, Any?> {
      return mapOf(
          UID to uid,
          USERNAME to username,
          FOLLOWING to following,
          FOLLOWERS to followers,
          PENDING_REQUESTS to pendingRequests.toList(),
          FIRST_NAME to firstName,
          LAST_NAME to lastName,
          EMAIL to email,
          PHONE_NUMBER to phoneNumber,
          COUNTRY to country,
          JOINED_EVENTS to joinedEvents,
          MY_EVENTS to myEvents,
          MY_FAVORITES to myFavorites,
          PROFILE_PICTURE to profilePicture,
          TAGS to tags,
          RATING to rating)
    }

    /**
     * Convert a map to a GoMeetUser.
     *
     * @param id the user id
     * @return the GoMeetUser
     */
    private fun Map<String, Any>.toUser(id: String): GoMeetUser {
        val ratingMap = this[RATING] as? Map<String, Long>
        val ratingFst = ratingMap?.get("first") ?: 0L
        val ratingSnd = ratingMap?.get("second") ?: 0L
      return GoMeetUser(
          uid = id,
          username = this[USERNAME] as String,
          following = (this[FOLLOWING] as? List<String>) ?: emptyList(),
          followers = (this[FOLLOWERS] as? List<String>) ?: emptyList(),
          pendingRequests = convertToInvitationsList(this[PENDING_REQUESTS]).toSet(),
          firstName = this[FIRST_NAME] as? String ?: "",
          lastName = this[LAST_NAME] as? String ?: "",
          email = this[EMAIL] as? String ?: "",
          phoneNumber = this[PHONE_NUMBER] as? String ?: "",
          profilePicture = this[PROFILE_PICTURE] as? String ?: "",
          country = this[COUNTRY] as? String ?: "",
          joinedEvents = (this[JOINED_EVENTS] as? List<String>) ?: emptyList(),
          myEvents = (this[MY_EVENTS] as? List<String>) ?: emptyList(),
          myFavorites = (this[MY_FAVORITES] as? List<String>) ?: emptyList(),
          tags = (this[TAGS] as? List<String>) ?: emptyList(),
          rating = Pair(ratingFst, ratingSnd))
    }

    private fun convertToInvitationsList(data: Any?): List<Invitation> {
      if (data is List<*>) {
        return data.mapNotNull { element ->
          if (element is Map<*, *>) {
            val eventId = element["eventId"] as? String
            val status = element["status"] as? String
            if (eventId != null && status != null) {
              Invitation(eventId, InviteStatus.valueOf(status))
            } else {
              null
            }
          } else {
            null
          }
        }
      }
      return emptyList()
    }
  }
}
