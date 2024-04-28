package com.github.se.gomeet.model.repository

import android.util.Log
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.location.Location
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate

/**
 * This class represents the repository for the events.
 * A repository is a class that communicates with the data source.
 *
 * @param db The database firebase instance
 */
class EventRepository(private val db: FirebaseFirestore) {

  private val localEventsList: MutableList<Event> = mutableListOf()

    /**
     * This function initializes the repository by starting to listen for events
     */
  init {
    startListeningForEvents()
  }

    /**
     * This companion object contains the constants for the repository
     */
  companion object {
    private const val TAG = "FirebaseConnection"
    private const val EVENT_COLLECTION = "events"
  }

    /**
     * This function retrieves an event ID stored in the database
     *
     * @return the new id for the event
     */
  fun getNewId(): String {
    return db.collection(EVENT_COLLECTION).document().id
  }

    /**
     * This function retrieves an event from the database
     *
     * @param uid The event ID
     * @param callback The callback function to be called when the event is retrieved
     *
     * @return the event if it exists, null otherwise
     */
  fun getEvent(uid: String, callback: (Event?) -> Unit) {
    db.collection(EVENT_COLLECTION)
        .document(uid)
        .get()
        .addOnSuccessListener { document ->
          if (document != null && document.exists()) {
            val event = document.data!!.toEvent(uid)
            callback(event)
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
     * This function retrieves all the events from the database
     *
     * @param callback The callback function to be called when the events are retrieved
     *
     * @return the list of events if they exist, null otherwise
     */
  fun getAllEvents(callback: (List<Event>?) -> Unit) {
    db.collection(EVENT_COLLECTION)
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
    db.collection(EVENT_COLLECTION)
        .document(event.uid)
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
    val documentRef = db.collection(EVENT_COLLECTION).document(event.uid)
    documentRef
        .update(event.toMap())
        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
  }

    /**
     * This function removes an event from the database
     *
     * @param uid The event ID
     */
  fun removeEvent(uid: String) {
    db.collection(EVENT_COLLECTION)
        .document(uid)
        .delete()
        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
  }

    /**
     * This function maps descriptions of parameters of an event to the fields stored in the database
     *
     * @return the map of the event
     */
  private fun Event.toMap(): Map<String, Any?> {
    return mapOf(
        "uid" to uid,
        "creator" to creator,
        "title" to title,
        "description" to description,
        "location" to location.toMap(),
        "date" to date.toString(),
        "price" to price,
        "url" to url,
        "participants" to participants,
        "visibleToIfPrivate" to visibleToIfPrivate,
        "maxParticipants" to maxParticipants,
        "public" to public,
        "tags" to tags,
        "images" to images)
  }

    /**
     * This function maps the location of an event to the fields stored in the database
     *
     * @return the map of the location
     */
  private fun Location.toMap(): Map<String, Any> {
    return mapOf("latitude" to latitude, "longitude" to longitude, "name" to name)
  }

    /**
     * This function maps the fields stored in the database to the event
     *
     * @return the event
     */
  private fun Map<String, Any>.toEvent(id: String? = null): Event {
    return Event(
        uid = id ?: this["uid"] as? String ?: "",
        creator = this["creator"] as? String ?: "",
        title = this["title"] as? String ?: "",
        description = this["description"] as? String ?: "",
        location = (this["location"] as? Map<String, Any>)?.toLocation() ?: Location(.0, .0, ""),
        date = LocalDate.parse(this["date"] as? String ?: ""),
        price = this["price"] as? Double ?: 0.0,
        url = this["url"] as? String ?: "",
        participants = this["participants"] as? List<String> ?: emptyList(),
        visibleToIfPrivate = this["visibleToIfPrivate"] as? List<String> ?: emptyList(),
        maxParticipants = (this["maxParticipants"] as? String)?.toIntOrNull() ?: 0,
        public = this["public"] as? Boolean ?: false,
        tags = this["tags"] as? List<String> ?: emptyList(),
        images = this["images"] as? List<String> ?: emptyList())
  }

    /**
     * This function maps the fields stored in the database to the location
     *
     * @return the location
     */
  private fun Map<String, Any>.toLocation(): Location {
    return Location(
        latitude = this["latitude"] as Double,
        longitude = this["longitude"] as Double,
        name = this["name"] as String)
  }

    /**
     * This function starts listening for events in the database and updates the event lists stored
     * in the database accordingly to what this function listens to
     */
  private fun startListeningForEvents() {
    db.collection("events").addSnapshotListener { snapshot, e ->
      if (e != null) {
        // Handle error
        Log.w("EventRepository", "Listen failed.", e)
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
      }
    }
  }
}
