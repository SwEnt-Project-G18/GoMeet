package com.github.se.gomeet.model.repository

import android.util.Log
import com.github.se.gomeet.model.event.*
import com.github.se.gomeet.model.event.location.*
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.tasks.await

/**
 * This class represents the repository for the events. A repository is a class that communicates
 * with the data source.
 */
class EventRepository private constructor() {

  /** This companion object contains the functions in the repository */
  companion object {

    /** This function initialises the repository by starting to listen for events */
    fun init(cid: String) {
      startListeningForEvents()
      creatorId = cid
    }

    private val localEventsList: MutableList<Event> = mutableListOf()
    private const val TAG = "EventRepository"
    private const val EVENT_COLLECTION = "events"
    private lateinit var creatorId: String

    /**
     * This function retrieves an event ID stored in the database
     *
     * @return the new id for the event
     */
    fun getNewId(): String {
      return Firebase.firestore.collection(EVENT_COLLECTION).document().id
    }

    /**
     * This function retrieves an event from the database
     *
     * @param uid The event ID
     * @param callback The callback function to be called when the event is retrieved
     * @return the event if it exists, null otherwise
     */
    fun getEvent(uid: String, callback: (Event?) -> Unit) {
      Firebase.firestore
          .collection(EVENT_COLLECTION)
          .document(uid)
          .get()
          .addOnSuccessListener { document ->
            if (document != null && document.exists()) {
              val event = document.data!!.toEvent(uid)
              callback(event)
            } else {
              Log.w(TAG, "No such document")
              callback(null)
            }
          }
          .addOnFailureListener { exception ->
            Log.e(TAG, "getEvent() failed", exception)
            callback(null)
          }
    }

    /**
     * This function retrieves all the events from the database
     *
     * @param callback The callback function to be called when the events are retrieved
     * @return the list of events if they exist, null otherwise
     */
    fun getAllEvents(callback: (List<Event>?) -> Unit) {
      Firebase.firestore
          .collection(EVENT_COLLECTION)
          .get()
          .addOnSuccessListener { querySnapshot ->
            val eventList = mutableListOf<Event>()
            for (document in querySnapshot.documents) {
              val event = document.data?.toEvent(document.id)
              if (event != null) {
                eventList.add(event)
              }
            }
            callback(eventList)
          }
          .addOnFailureListener { exception ->
            Log.w(TAG, "Error getting documents.", exception)
            callback(null)
          }
    }

    /**
     * This function adds an event to the database
     *
     * @param event The event to be added
     */
    fun addEvent(event: Event) {
      Firebase.firestore
          .collection(EVENT_COLLECTION)
          .document(event.eventID)
          .set(event.toMap())
          .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
          .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
    }

    /**
     * This function updates an event in the database
     *
     * @param event The event to be updated
     */
    fun updateEvent(event: Event) {
      val documentRef = Firebase.firestore.collection(EVENT_COLLECTION).document(event.eventID)
      documentRef
          .update(event.toMap())
          .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
          .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
    }

    suspend fun sendInvitation(eventID: String, userId: String) {
      val event = Firebase.firestore.collection(EVENT_COLLECTION).document(eventID).get().await()
      val pendingParticipants = event.get(PENDING_PARTICIPANTS) as MutableList<String>
      if (pendingParticipants.contains(userId)) {
        Log.w(TAG, "Event $eventID already has $userId as a pendingParticipant")
        return
      }
      pendingParticipants.add(userId)
      Firebase.firestore
          .collection(EVENT_COLLECTION)
          .document(eventID)
          .update(PENDING_PARTICIPANTS, pendingParticipants)
    }

    /**
     * This function updates the rating of an event by a particular user
     *
     * @param eventID The event ID
     * @param newRating The new rating
     * @param currentUID The rater's user ID
     * @param oldRating The old rating
     * @param organiserID The id of the organiser of the event
     */
    suspend fun updateRating(
        eventID: String,
        newRating: Long,
        currentUID: String,
        oldRating: Long,
        organiserID: String
    ) {
      if (currentUID == organiserID) return
      val ratings =
          Firebase.firestore
              .collection(EVENT_COLLECTION)
              .document(eventID)
              .get()
              .await()
              .get(RATINGS) as MutableMap<String, Long>
      ratings[currentUID] = newRating
      UserRepository.updateUserRating(organiserID, newRating, oldRating)
      val documentRef = Firebase.firestore.collection(EVENT_COLLECTION).document(eventID)
      documentRef
          .update(RATINGS, ratings)
          .addOnSuccessListener { Log.d(TAG, "Rating of event $eventID was successfully updated!") }
          .addOnFailureListener { e -> Log.w(TAG, "Error updating rating of event $eventID", e) }
    }

    /**
     * This function removes an event from the database
     *
     * @param uid The event ID
     */
    fun removeEvent(uid: String) {
      Firebase.firestore
          .collection(EVENT_COLLECTION)
          .document(uid)
          .delete()
          .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
          .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }

    /**
     * This function maps descriptions of parameters of an event to the fields stored in the
     * database
     *
     * @return the map of the event
     */
    private fun Event.toMap(): Map<String, Any?> {
      return mapOf(
          EVENT_ID to eventID,
          CREATOR to creator,
          TITLE to title,
          DESCRIPTION to description,
          LOCATION to location.toMap(),
          DATE to date.toString(),
          TIME to time.toString(),
          PRICE to price,
          URL to url,
          PENDING_PARTICIPANTS to pendingParticipants,
          PARTICIPANTS to participants,
          VISIBLE_TO_IF_PRIVATE to visibleToIfPrivate,
          MAX_PARTICIPANTS to maxParticipants,
          PUBLIC to public,
          TAGS to tags,
          IMAGES to images,
          RATINGS to ratings,
          POSTS to posts.map { it.toMap() })
    }

    /**
     * This function maps the location of an event to the fields stored in the database
     *
     * @return the map of the location
     */
    private fun Location.toMap(): Map<String, Any> {
      return mapOf(LATITUDE to latitude, LONGITUDE to longitude, NAME to name)
    }

    /**
     * This function maps the location of an event to the fields stored in the database
     *
     * @return the map of the location
     */
    private fun Post.toMap(): Map<String, Any> {
      return mapOf(
          "userId" to userId,
          "title" to title,
          "content" to content,
          "date" to date.toString(), // Convert LocalDate to String
          "time" to time.toString(), // Convert LocalTime to String
          "image" to image,
          "likes" to likes,
          "comments" to comments.associate { it.first to it.second })
    }

    /**
     * This function maps the fields stored in the database to the event
     *
     * @return the event
     */
    private fun Map<String, Any>.toEvent(id: String? = null): Event {
      return Event(
          eventID = id ?: this[EVENT_ID] as? String ?: "",
          creator = this[CREATOR] as? String ?: "",
          title = this[TITLE] as? String ?: "",
          description = this[DESCRIPTION] as? String ?: "",
          location = (this[LOCATION] as? Map<String, Any>)?.toLocation() ?: Location(.0, .0, ""),
          date = LocalDate.parse(this[DATE] as? String ?: ""),
          time = LocalTime.parse(this[TIME] as? String ?: "00:00"),
          price = this[PRICE] as? Double ?: 0.0,
          url = this[URL] as? String ?: "",
          pendingParticipants = this[PENDING_PARTICIPANTS] as? List<String> ?: emptyList(),
          participants = this[PARTICIPANTS] as? List<String> ?: emptyList(),
          visibleToIfPrivate = this[VISIBLE_TO_IF_PRIVATE] as? List<String> ?: emptyList(),
          maxParticipants = (this[MAX_PARTICIPANTS] as? String)?.toIntOrNull() ?: 0,
          public = this[PUBLIC] as? Boolean ?: false,
          tags = this[TAGS] as? List<String> ?: emptyList(),
          images = this[IMAGES] as? List<String> ?: emptyList(),
          ratings = this[RATINGS] as? Map<String, Long> ?: emptyMap(),
          posts = (this[POSTS] as? List<Map<String, Any>> ?: emptyList()).map { it.toPost() })
    }

    /**
     * This function maps the fields stored in the database to the location
     *
     * @return the location
     */
    private fun Map<String, Any>.toLocation(): Location {
      return Location(
          latitude = this[LATITUDE] as Double,
          longitude = this[LONGITUDE] as Double,
          name = this[NAME] as String)
    }

    private fun Map<String, Any>.toPost(): Post {
      val commentsMap = this["comments"] as? Map<String, String> ?: emptyMap()
      val commentsList = commentsMap.map { Pair(it.key, it.value) }

      return Post(
          userId = this["userId"] as? String ?: "",
          title = this["title"] as? String ?: "",
          content = this["content"] as? String ?: "",
          date = LocalDate.parse(this["date"] as? String ?: ""),
          time = LocalTime.parse(this["time"] as? String ?: "00:00"),
          image = this["image"] as? String ?: "",
          likes = this["likes"] as? List<String> ?: emptyList(),
          comments = commentsList)
    }

    /**
     * This function starts listening for events in the database and updates the event lists stored
     * in the database accordingly to what this function listens to
     */
    private fun startListeningForEvents() {
      Firebase.firestore.collection(EVENT_COLLECTION).addSnapshotListener(
          MetadataChanges.INCLUDE) { snapshot, e ->
            if (e != null) {
              // Handle error
              Log.w(TAG, "Listen failed.", e)
              return@addSnapshotListener
            }

            for (docChange in snapshot?.documentChanges!!) {

              val event = docChange.document.data.toEvent()
              when (docChange.type) {
                DocumentChange.Type.ADDED -> {
                  localEventsList.add(event)
                }
                DocumentChange.Type.MODIFIED -> {
                  localEventsList
                      .find { it == event }
                      ?.let { localEventsList[localEventsList.indexOf(it)] = event }
                }
                DocumentChange.Type.REMOVED -> {
                  localEventsList.removeIf { it == event }
                }
              }

              val source =
                  if (snapshot.metadata.isFromCache) {
                    "local cache"
                  } else {
                    "server"
                  }

              Log.d(TAG, "Data fetched from $source")
            }
          }
    }
  }
}
