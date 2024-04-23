package com.github.se.gomeet.model.repository

import android.util.Log
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.location.Location
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate

class EventRepository(private val db: FirebaseFirestore) {

  private val localEventsList: MutableList<Event> = mutableListOf()

  init {
    startListeningForEvents()
  }

  companion object {
    private const val TAG = "FirebaseConnection"
    private const val EVENT_COLLECTION = "events"
  }

  fun getNewId(): String {
    return db.collection(EVENT_COLLECTION).document().id
  }

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

  fun addEvent(event: Event) {
    db.collection(EVENT_COLLECTION)
        .document(event.uid)
        .set(event.toMap())
        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
  }

  fun updateEvent(event: Event) {
    val documentRef = db.collection(EVENT_COLLECTION).document(event.uid)
    documentRef
        .update(event.toMap())
        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
  }

  fun removeEvent(uid: String) {
    db.collection(EVENT_COLLECTION)
        .document(uid)
        .delete()
        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
  }

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

  private fun Location.toMap(): Map<String, Any> {
    return mapOf("latitude" to latitude, "longitude" to longitude, "name" to name)
  }

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

  private fun Map<String, Any>.toLocation(): Location {
    return Location(
        latitude = this["latitude"] as Double,
        longitude = this["longitude"] as Double,
        name = this["name"] as String)
  }

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